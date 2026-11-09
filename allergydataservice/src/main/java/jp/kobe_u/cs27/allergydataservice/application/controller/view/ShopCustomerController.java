package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuFilterConditionForm;
import jp.kobe_u.cs27.allergydataservice.application.form.MenuFilterForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuCategory;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Shop;
import jp.kobe_u.cs27.allergydataservice.domain.entity.User;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuAllergenService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuCategoryService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuFilterService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuJudgementService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuOriginService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuService;
import jp.kobe_u.cs27.allergydataservice.domain.service.ShopService;
import jp.kobe_u.cs27.allergydataservice.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 客がQRコード経由で閲覧する、店舗メニューの読み取り専用画面を操作するコントローラークラス
 * 店主側(ShopController/MenuController)とは異なり、ログイン・編集操作は一切行わない
 */
@Controller
@RequiredArgsConstructor
public class ShopCustomerController {

  private final ShopService shopService;
  private final MenuCategoryService categoryService;
  private final MenuService menuService;
  private final MenuAllergenService menuAllergenService;
  private final MenuOriginService menuOriginService;
  private final MenuJudgementService menuJudgementService;
  private final MenuFilterService menuFilterService;
  private final UserService userService;

  /**
   * コンタミ完全除去不可の注意書き画面を表示する
   * 店舗が掲示するQRコードの読み取り先
   *
   * @param shopId 店舗ログインID
   * @param model
   * @return 注意書きページ
   */
  @GetMapping("/shop/{shopId}/customer/notice")
  public String showNoticePage(
      @PathVariable("shopId") String shopId,
      Model model) {

    // 店舗情報をDBから取得する
    Shop shop = shopService.getShop(shopId);

    model.addAttribute(
        "shopId",
        shop.getShopId());
    model.addAttribute(
        "shopName",
        shop.getShopName());

    // 注意書きページ
    return "menuNotice";
  }

  /**
   * 客向けの読み取り専用メニュー一覧ページを表示する
   * 店主用のshopMainページと異なり、カテゴリ追加・メニュー追加・ログアウト等の操作は表示しない
   * filterActiveがtrueの場合、絞り込み条件に基づいた判定バッジ(○/△/×)を各メニューに表示する
   *
   * @param shopId          店舗ログインID
   * @param uid             ユーザID(絞り込み画面への導線や、絞り込み結果の引き継ぎに使う。任意)
   * @param filterActive    絞り込み結果を表示するかどうか
   * @param filterAllergenId 絞り込み対象のアレルゲンidの一覧(filterToleranceと対応)
   * @param filterTolerance  絞り込み対象のコンタミ許容度の一覧(filterAllergenIdと対応)
   * @param model
   * @return 客向けメニュー一覧ページ
   */
  @GetMapping("/shop/{shopId}/customer/main")
  public String showMenuListPage(
      @PathVariable("shopId") String shopId,
      @RequestParam(value = "uid", required = false) String uid,
      @RequestParam(value = "filterActive", defaultValue = "false") boolean filterActive,
      @RequestParam(value = "filterAllergenId", required = false) List<Long> filterAllergenId,
      @RequestParam(value = "filterTolerance", required = false) List<Integer> filterTolerance,
      Model model) {

    // 店舗情報をDBから取得する
    Shop shop = shopService.getShop(shopId);

    // 店舗が作成したカテゴリの一覧をDBから取得する
    List<MenuCategory> categories = categoryService.getCategoriesByShop(shopId);

    // カテゴリごとに登録済みのメニュー一覧をDBから取得する
    Map<Long, List<Menu>> menusByCategory = new LinkedHashMap<>();
    List<Menu> allMenus = new ArrayList<>();
    for (MenuCategory category : categories) {
      List<Menu> menus = menuService.getMenusByCategory(category.getCategoryId());
      menusByCategory.put(
          category.getCategoryId(),
          menus);
      allMenus.addAll(menus);
    }

    model.addAttribute(
        "shopId",
        shop.getShopId());
    model.addAttribute(
        "shopName",
        shop.getShopName());
    model.addAttribute(
        "categories",
        categories);
    model.addAttribute(
        "menusByCategory",
        menusByCategory);
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "filterActive",
        filterActive);
    model.addAttribute(
        "filterAllergenId",
        filterAllergenId);
    model.addAttribute(
        "filterTolerance",
        filterTolerance);

    // 絞り込み結果を表示する場合、アレルゲンidごとのコンタミ許容度を組み立ててメニューごとに判定する
    if (filterActive && filterAllergenId != null && filterTolerance != null) {
      Map<Long, Integer> toleranceByAllergenid = buildToleranceMap(filterAllergenId, filterTolerance);
      Map<Long, Integer> judgementByMenuId = menuJudgementService.judgeMenus(allMenus, toleranceByAllergenid);

      Map<Long, String> markByMenuId = new LinkedHashMap<>();
      judgementByMenuId.forEach((menuId, code) -> markByMenuId.put(menuId, menuJudgementService.toMark(code)));

      model.addAttribute(
          "menuJudgementMarks",
          markByMenuId);
      model.addAttribute(
          "filterConditionSummaries",
          menuFilterService.buildConditionSummaries(toleranceByAllergenid));
    }

    // 客向けメニュー一覧ページ
    return "menuViewerMain";
  }

  /**
   * 客向けの読み取り専用メニュー詳細ページを表示する
   * 店主用のmenuDetailViewerページと異なり、編集・削除ボタンは表示しない
   * filterActiveがtrueの場合、絞り込み条件に基づいた判定バッジ(○/△/×)を表示する
   *
   * @param shopId          店舗ログインID
   * @param menuId          メニューId
   * @param uid             ユーザID(一覧画面から引き継ぐ。任意)
   * @param filterActive    絞り込み結果を表示するかどうか
   * @param filterAllergenId 絞り込み対象のアレルゲンidの一覧
   * @param filterTolerance  絞り込み対象のコンタミ許容度の一覧
   * @param model
   * @return 客向けメニュー詳細ページ
   */
  @GetMapping("/shop/{shopId}/customer/menu/{menuId}")
  public String showMenuDetailPage(
      @PathVariable("shopId") String shopId,
      @PathVariable("menuId") Long menuId,
      @RequestParam(value = "uid", required = false) String uid,
      @RequestParam(value = "filterActive", defaultValue = "false") boolean filterActive,
      @RequestParam(value = "filterAllergenId", required = false) List<Long> filterAllergenId,
      @RequestParam(value = "filterTolerance", required = false) List<Integer> filterTolerance,
      Model model) {

    // メニュー情報をDBから取得する
    Menu menu = menuService.getMenu(menuId);

    // カテゴリ情報をDBから取得する
    MenuCategory category = categoryService.getCategory(menu.getCategoryId());

    model.addAttribute(
        "shopId",
        shopId);
    model.addAttribute(
        "menu",
        menu);
    model.addAttribute(
        "categoryName",
        category.getCategoryName());
    model.addAttribute(
        "allergenForms",
        menuAllergenService.buildSelectionFormsForMenu(menuId));
    model.addAttribute(
        "mandatoryAllergenCount",
        menuAllergenService.getMandatoryAllergenCount());
    model.addAttribute(
        "origins",
        menuOriginService.getOriginsByMenu(menuId));
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "filterActive",
        filterActive);
    model.addAttribute(
        "filterAllergenId",
        filterAllergenId);
    model.addAttribute(
        "filterTolerance",
        filterTolerance);

    if (filterActive && filterAllergenId != null && filterTolerance != null) {
      Map<Long, Integer> toleranceByAllergenid = buildToleranceMap(filterAllergenId, filterTolerance);
      int judgement = menuJudgementService.judgeMenu(menuId, toleranceByAllergenid);

      model.addAttribute(
          "menuJudgementMark",
          menuJudgementService.toMark(judgement));
    }

    // 客向けメニュー詳細ページ
    return "menuDetailPublicViewer";
  }

  /**
   * 客向けのアレルギー確認・絞り込み条件設定画面を表示する
   * uidが未指定の場合はuid入力のみを表示し、uidが確定している場合は
   * 登録済みアレルギーから導いた既定の絞り込み条件を表示する
   *
   * @param shopId 店舗ログインID
   * @param uid    ユーザID(任意)
   * @param model
   * @return 絞り込み条件設定ページ
   */
  @GetMapping("/shop/{shopId}/customer/filter")
  public String showFilterPage(
      @PathVariable("shopId") String shopId,
      @RequestParam(value = "uid", required = false) String uid,
      Model model) {

    // 店舗情報をDBから取得する
    Shop shop = shopService.getShop(shopId);

    model.addAttribute(
        "shopId",
        shop.getShopId());
    model.addAttribute(
        "shopName",
        shop.getShopName());

    // uidが未指定の場合、uid入力フォームのみ表示する
    if (uid == null || uid.isBlank()) {
      return "menuFilterPage";
    }

    // uidが指定されている場合、ユーザの存在を確認する
    User user;
    try {
      user = userService.getUser(uid);
    } catch (UserValidationException e) {
      // ユーザが存在しない場合、エラーフラグをオンにしてuid入力フォームを再表示する
      model.addAttribute(
          "isUserDoesNotExistError",
          true);
      return "menuFilterPage";
    }

    // 絞り込み画面に表示する主要29品目すべての条件を組み立てる(登録済みアレルギーがあれば選択済み状態になる)
    List<MenuFilterConditionForm> conditions = menuFilterService.buildAllConditions(uid);
    boolean hasDefaultCondition = conditions.stream().anyMatch(MenuFilterConditionForm::isSelected);

    model.addAttribute(
        "username",
        user.getUsername());
    model.addAttribute(
        "hasDefaultCondition",
        hasDefaultCondition);
    model.addAttribute(new MenuFilterForm(
        uid,
        conditions));

    return "menuFilterPage";
  }

  /**
   * 絞り込み条件を確定し、判定結果付きのメニュー一覧ページへ遷移する
   *
   * @param shopId     店舗ログインID
   * @param form       MenuFilterForm
   * @param attributes
   * @return 客向けメニュー一覧ページ(判定結果表示)
   */
  @PostMapping("/shop/{shopId}/customer/filter/result")
  public String applyFilter(
      @PathVariable("shopId") String shopId,
      @ModelAttribute MenuFilterForm form,
      RedirectAttributes attributes) {

    Map<Long, Integer> toleranceByAllergenid = menuFilterService.resolveToleranceMap(form);

    List<Long> filterAllergenId = new ArrayList<>(toleranceByAllergenid.keySet());
    List<Integer> filterTolerance = new ArrayList<>();
    for (Long allergenid : filterAllergenId) {
      filterTolerance.add(toleranceByAllergenid.get(allergenid));
    }

    attributes.addAttribute(
        "shopId",
        shopId);
    attributes.addAttribute(
        "uid",
        form.getUid());
    attributes.addAttribute(
        "filterActive",
        true);
    attributes.addAttribute(
        "filterAllergenId",
        filterAllergenId);
    attributes.addAttribute(
        "filterTolerance",
        filterTolerance);

    // 客向けメニュー一覧ページ(判定結果表示)
    return "redirect:/shop/{shopId}/customer/main";
  }

  /**
   * アレルゲンidの一覧とコンタミ許容度の一覧(対応するインデックス同士が1組)から、
   * アレルゲンidごとのコンタミ許容度のMapを組み立てる
   *
   * @param filterAllergenId 絞り込み対象のアレルゲンidの一覧
   * @param filterTolerance  絞り込み対象のコンタミ許容度の一覧
   * @return アレルゲンidごとのコンタミ許容度のMap
   */
  private Map<Long, Integer> buildToleranceMap(List<Long> filterAllergenId, List<Integer> filterTolerance) {

    Map<Long, Integer> toleranceByAllergenid = new LinkedHashMap<>();

    int size = Math.min(filterAllergenId.size(), filterTolerance.size());
    for (int i = 0; i < size; i++) {
      toleranceByAllergenid.put(filterAllergenId.get(i), filterTolerance.get(i));
    }

    return toleranceByAllergenid;
  }

}
