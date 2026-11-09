package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuCategoryForm;
import jp.kobe_u.cs27.allergydataservice.application.form.ShopForm;
import jp.kobe_u.cs27.allergydataservice.application.form.ShopLoginForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.ShopValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuCategory;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Shop;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuCategoryService;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuService;
import jp.kobe_u.cs27.allergydataservice.domain.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 店舗操作を行うコントローラークラス
 * Thymeleafに渡す値をModelに登録し、ページを表示する
 */
@Controller
@RequiredArgsConstructor
public class ShopController {

  private final ShopService service;
  private final MenuCategoryService categoryService;
  private final MenuService menuService;

  /**
   * 店舗ログインページを表示する
   *
   * @param model
   * @return 店舗ログインページ
   */
  @GetMapping("/shop/login")
  public String showShopLoginPage(Model model) {

    // ShopLoginFormをModelに追加する(Thymeleaf上ではshopLoginForm)
    model.addAttribute(new ShopLoginForm());

    return "shopLogin";
  }

  /**
   * 店舗ログインIDとパスワードを照合し、ログインする
   *
   * @param model
   * @param attributes
   * @param form ShopLoginForm
   * @return 店舗メインページ、または店舗ログインページ
   */
  @PostMapping("/shop/login")
  public String loginShop(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          ShopLoginForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isShopLoginFormError",
          true);

      // 店舗ログインページ
      return "redirect:/shop/login";
    }

    // 店舗ログインIDとパスワードを照合する
    Shop shop;
    try {
      shop = service.authenticate(
          form.getShopId(),
          form.getPassword());
    } catch (ShopValidationException e) {
      // 店舗が存在しない、またはパスワードが一致しなかった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isShopLoginFailedError",
          true);

      // 店舗ログインページ
      return "redirect:/shop/login";
    }

    // リダイレクト先の引数として店舗ログインIDを渡す
    attributes.addAttribute(
        "shopId",
        shop.getShopId());

    // 店舗メインページ
    return "redirect:/shop/{shopId}/main";
  }

  /**
   * 店舗新規登録ページを表示する
   *
   * @param model
   * @return 店舗新規登録ページ
   */
  @GetMapping("/shop/signup")
  public String showShopRegistrationPage(Model model) {

    // ShopFormをModelに追加する(Thymeleaf上ではshopForm)
    model.addAttribute(new ShopForm());

    return "shopRegister";
  }

  /**
   * 店舗登録が可能か確認する
   * 店舗ログインIDが登録済み、またはパスワード確認が一致しない場合は登録ページに戻る
   *
   * @param model
   * @param attributes
   * @param form ShopForm
   * @return 店舗登録確認ページ、または店舗新規登録ページ
   */
  @PostMapping("/shop/register/confirm")
  public String confirmShopRegistration(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          ShopForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isShopFormError",
          true);

      // 店舗新規登録ページ
      return "redirect:/shop/signup";
    }

    // パスワードとパスワード確認が一致しない場合
    if (!form.getPassword().equals(form.getPasswordConfirm())) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isPasswordConfirmError",
          true);

      // 店舗新規登録ページ
      return "redirect:/shop/signup";
    }

    // 店舗ログインIDを変数に格納する
    final String shopId = form.getShopId();

    // 店舗が既に存在するか確認する
    if (service.existsShop(shopId)) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isShopAlreadyExistsError",
          true);

      // 店舗新規登録ページ
      return "redirect:/shop/signup";
    }

    // 店舗情報をModelに追加する
    model.addAttribute(
        "shopId",
        shopId);
    model.addAttribute(
        "shopName",
        form.getShopName());
    model.addAttribute(
        "password",
        form.getPassword());

    // 店舗登録確認ページ
    return "shopConfirmRegistration";
  }

  /**
   * 店舗を登録する
   *
   * @param model
   * @param attributes
   * @param form ShopForm
   * @return 店舗登録完了ページ
   */
  @PostMapping("/shop/register")
  public String registerShop(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          ShopForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isShopFormError",
          true);

      // 店舗新規登録ページ
      return "redirect:/shop/signup";
    }

    // 店舗を登録する
    Shop shop;
    try {
      shop = service.createShop(form);
    } catch (ShopValidationException e) {
      // 店舗が登録済みであった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isShopAlreadyExistsError",
          true);

      // 店舗新規登録ページ
      return "redirect:/shop/signup";
    }

    // 店舗ログインIDと店舗名をModelに追加する
    model.addAttribute(
        "shopId",
        shop.getShopId());
    model.addAttribute(
        "shopName",
        shop.getShopName());

    // 店舗登録完了ページ
    return "shopRegistrationComplete";
  }

  /**
   * 店舗のメインページを表示する
   *
   * @param shopId 店舗ログインID
   * @param model
   * @return 店舗メインページ
   */
  @GetMapping("/shop/{shopId}/main")
  public String showShopMainPage(
      @PathVariable("shopId") String shopId,
      Model model) {

    // 店舗情報をDBから取得する
    Shop shop = service.getShop(shopId);

    // 店舗が作成したカテゴリの一覧をDBから取得する
    List<MenuCategory> categories = categoryService.getCategoriesByShop(shopId);

    // カテゴリごとに登録済みのメニュー一覧をDBから取得する
    Map<Long, List<Menu>> menusByCategory = new LinkedHashMap<>();
    for (MenuCategory category : categories) {
      menusByCategory.put(
          category.getCategoryId(),
          menuService.getMenusByCategory(category.getCategoryId()));
    }

    // 店舗ログインIDと店舗名をModelに追加する
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

    // MenuCategoryFormをModelに追加する(Thymeleaf上ではmenuCategoryForm)
    if (!model.containsAttribute("menuCategoryForm")) {
      model.addAttribute(new MenuCategoryForm());
    }

    // 店舗メインページ
    return "shopMain";
  }

  /**
   * 店舗が客に提示するQRコードの表示ページを表示する
   * QRコードは客がスマホの標準カメラで読み取ることを想定し、客向けメニュー閲覧ページ(注意書き画面)のURLを埋め込む
   *
   * @param shopId 店舗ログインID
   * @param model
   * @return QRコード表示ページ
   */
  @GetMapping("/shop/{shopId}/qr")
  public String showShopQRPage(
      @PathVariable("shopId") String shopId,
      Model model) {

    // 店舗情報をDBから取得する
    Shop shop = service.getShop(shopId);

    // QRコードに埋め込む客向けメニュー閲覧ページのURL
    String qrDataUrl = "https://es4.eedept.kobe-u.ac.jp/allergydataservice/shop/" + shopId + "/customer/notice";

    model.addAttribute(
        "shopId",
        shop.getShopId());
    model.addAttribute(
        "shopName",
        shop.getShopName());
    model.addAttribute(
        "qrDataUrl",
        qrDataUrl);

    // 店舗QRコード表示ページ
    return "shopQRPage";
  }

  /**
   * 店舗退会が可能か確認する
   *
   * @param shopId 店舗ログインID
   * @param model
   * @return 店舗退会確認ページ
   */
  @GetMapping("/shop/{shopId}/delete/confirm")
  public String confirmShopDelete(
      @PathVariable("shopId") String shopId,
      Model model) {

    // 店舗情報をDBから取得する
    Shop shop = service.getShop(shopId);

    model.addAttribute(
        "shopId",
        shop.getShopId());
    model.addAttribute(
        "shopName",
        shop.getShopName());

    // 店舗退会確認ページ
    return "shopConfirmDelete";
  }

  /**
   * 店舗を削除する
   *
   * @param shopId 店舗ログインID
   * @return 店舗ログインページ
   */
  @PostMapping("/shop/{shopId}/delete")
  public String deleteShop(
      @PathVariable("shopId") String shopId) {

    // 店舗を削除する
    service.deleteShop(shopId);

    // 店舗ログインページ
    return "redirect:/shop/login";
  }

}
