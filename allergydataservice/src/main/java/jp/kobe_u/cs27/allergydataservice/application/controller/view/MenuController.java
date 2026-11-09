package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuCategory;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuAllergenService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuCategoryService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuImageStorageService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuOriginService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * メニュー操作を行うコントローラークラス
 */
@Controller
@RequiredArgsConstructor
public class MenuController {

  private final MenuCategoryService categoryService;
  private final MenuAllergenService menuAllergenService;
  private final MenuOriginService menuOriginService;
  private final MenuService menuService;
  private final MenuImageStorageService imageStorageService;

  /**
   * メニュー登録ページを表示する
   *
   * @param shopId     店舗ログインID
   * @param categoryId カテゴリId
   * @param model
   * @return メニュー登録ページ
   */
  @GetMapping("/shop/{shopId}/menu/add/{categoryId}")
  public String showMenuRegistrationPage(
      @PathVariable("shopId") String shopId,
      @PathVariable("categoryId") Long categoryId,
      Model model) {

    // カテゴリ情報をDBから取得する
    MenuCategory category = categoryService.getCategory(categoryId);

    // 確認画面からの差し戻し(バリデーションエラー)でない場合は、フォームを新規作成する
    if (!model.containsAttribute("menuForm")) {
      MenuForm form = new MenuForm();
      form.setCategoryId(categoryId);
      form.setAllergenForms(menuAllergenService.buildEmptySelectionForms());
      model.addAttribute(form);
    }

    model.addAttribute(
        "shopId",
        shopId);
    model.addAttribute(
        "categoryId",
        categoryId);
    model.addAttribute(
        "categoryName",
        category.getCategoryName());
    model.addAttribute(
        "mandatoryAllergenCount",
        menuAllergenService.getMandatoryAllergenCount());

    return "menuRegister";
  }

  /**
   * メニュー登録が可能か確認する
   * この段階で画像をディスクに保存し、以降は保存先パスのみを引き継ぐ
   *
   * @param shopId 店舗ログインID
   * @param model
   * @param attributes
   * @param form MenuForm
   * @param bindingResult
   * @return メニュー登録確認ページ、またはメニュー登録ページ
   */
  @PostMapping("/shop/{shopId}/menu/add/confirm")
  public String confirmMenuRegistration(
      @PathVariable("shopId") String shopId,
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          MenuForm form,
      BindingResult bindingResult,
      @RequestParam(name = "mandatoryOnly", defaultValue = "false") boolean mandatoryOnly) {

    // リダイレクト先の引数として店舗ログインIDとカテゴリIdを渡す
    attributes.addAttribute(
        "shopId",
        shopId);
    attributes.addAttribute(
        "categoryId",
        form.getCategoryId());

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // 入力値とバリデーション結果を引き継ぎ、フォームを再表示する
      attributes.addFlashAttribute(
          "org.springframework.validation.BindingResult.menuForm",
          bindingResult);
      attributes.addFlashAttribute(
          "menuForm",
          form);
      attributes.addFlashAttribute(
          "isMenuFormError",
          true);

      // メニュー登録ページ
      return "redirect:/shop/{shopId}/menu/add/{categoryId}";
    }

    // 特定原材料(必須9品目)について、含有の有無が未回答のものがあった場合
    List<String> unansweredAllergenNames = menuAllergenService.getUnansweredContainsAllergenNames(
        form,
        mandatoryOnly);

    if (!unansweredAllergenNames.isEmpty()) {
      // 入力値と未回答の品目名を引き継ぎ、フォームを再表示する
      attributes.addFlashAttribute(
          "menuForm",
          form);
      attributes.addFlashAttribute(
          "isMenuAllergenError",
          true);
      attributes.addFlashAttribute(
          "unansweredAllergenNames",
          unansweredAllergenNames);
      attributes.addFlashAttribute(
          "mandatoryOnly",
          mandatoryOnly);

      // メニュー登録ページ
      return "redirect:/shop/{shopId}/menu/add/{categoryId}";
    }

    // アップロードされた画像をディスクに保存し、保存先パスをフォームに設定する
    String imagePath = imageStorageService.store(form.getImage());
    form.setImagePath(imagePath);

    // カテゴリ情報をDBから取得する
    MenuCategory category = categoryService.getCategory(form.getCategoryId());

    model.addAttribute(
        "shopId",
        shopId);
    model.addAttribute(
        "categoryName",
        category.getCategoryName());
    model.addAttribute(
        "mandatoryAllergenCount",
        menuAllergenService.getMandatoryAllergenCount());
    model.addAttribute(
        "menuForm",
        form);

    // メニュー登録確認ページ
    return "menuConfirmRegistration";
  }

  /**
   * メニューを登録する
   *
   * @param shopId 店舗ログインID
   * @param attributes
   * @param form MenuForm
   * @return 店舗メインページ
   */
  @PostMapping("/shop/{shopId}/menu/register")
  public String registerMenu(
      @PathVariable("shopId") String shopId,
      RedirectAttributes attributes,
      @ModelAttribute
          MenuForm form) {

    // メニュー(と、それに付随するアレルゲン紐付け・原産地情報)を登録する
    menuService.createMenu(form, shopId);

    // リダイレクト先の引数として店舗ログインIDを渡す
    attributes.addAttribute(
        "shopId",
        shopId);

    // 店舗メインページ
    return "redirect:/shop/{shopId}/main";
  }

  /**
   * メニューの詳細を表示する
   *
   * @param shopId 店舗ログインID
   * @param menuId メニューId
   * @param model
   * @return メニュー詳細ページ
   */
  @GetMapping("/shop/{shopId}/menu/{menuId}")
  public String showMenuDetailPage(
      @PathVariable("shopId") String shopId,
      @PathVariable("menuId") Long menuId,
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

    return "menuDetailViewer";
  }

  /**
   * メニューを削除する
   * 付随するアレルゲン紐付け・原産地情報も併せて削除する
   *
   * @param shopId 店舗ログインID
   * @param menuId メニューId
   * @param attributes
   * @return 店舗メインページ
   */
  @GetMapping("/shop/{shopId}/menu/delete/{menuId}")
  public String deleteMenu(
      @PathVariable("shopId") String shopId,
      @PathVariable("menuId") Long menuId,
      RedirectAttributes attributes) {

    menuService.deleteMenu(menuId);

    attributes.addAttribute(
        "shopId",
        shopId);

    return "redirect:/shop/{shopId}/main";
  }

}
