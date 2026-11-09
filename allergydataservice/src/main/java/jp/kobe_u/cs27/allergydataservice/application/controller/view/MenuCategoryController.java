package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuCategoryForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.ShopValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.service.MenuCategoryService;
import jp.kobe_u.cs27.allergydataservice.domain.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * メニューカテゴリ操作を行うコントローラークラス
 */
@Controller
@RequiredArgsConstructor
public class MenuCategoryController {

  private final MenuCategoryService service;
  private final ShopService shopService;

  /**
   * カテゴリを登録する
   *
   * @param shopId 店舗ログインID
   * @param attributes
   * @param form MenuCategoryForm
   * @param bindingResult
   * @return 店舗メインページ
   */
  @PostMapping("/shop/{shopId}/category/register")
  public String registerCategory(
      @PathVariable("shopId") String shopId,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          MenuCategoryForm form,
      BindingResult bindingResult) {

    // リダイレクト先の引数として店舗ログインIDを渡す
    attributes.addAttribute(
        "shopId",
        shopId);

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // 入力値とバリデーション結果を引き継ぎ、フォームを再表示する
      attributes.addFlashAttribute(
          "org.springframework.validation.BindingResult.menuCategoryForm",
          bindingResult);
      attributes.addFlashAttribute(
          "menuCategoryForm",
          form);
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isMenuCategoryFormError",
          true);

      // 店舗メインページ
      return "redirect:/shop/{shopId}/main";
    }

    // カテゴリを登録する
    service.createCategory(shopId, form);

    // 店舗メインページ
    return "redirect:/shop/{shopId}/main";
  }

  /**
   * カテゴリを削除する
   * 誤操作防止のため、店舗パスワードを再度照合し、一致した場合のみ削除を実行する
   *
   * @param shopId     店舗ログインID
   * @param categoryId カテゴリId
   * @param password   店舗パスワード(確認用、画面側のポップアップで入力させる)
   * @param attributes
   * @return 店舗メインページ
   */
  @PostMapping("/shop/{shopId}/category/delete/{categoryId}")
  public String deleteCategory(
      @PathVariable("shopId") String shopId,
      @PathVariable("categoryId") Long categoryId,
      @RequestParam("password") String password,
      RedirectAttributes attributes) {

    // リダイレクト先の引数として店舗ログインIDを渡す
    attributes.addAttribute(
        "shopId",
        shopId);

    // 店舗パスワードを照合する
    try {
      shopService.authenticate(shopId, password);
    } catch (ShopValidationException e) {
      // パスワードが一致しなかった場合、エラーフラグをオンにして店舗メインページに戻る
      attributes.addFlashAttribute(
          "isCategoryDeletePasswordError",
          true);

      // 店舗メインページ
      return "redirect:/shop/{shopId}/main";
    }

    // カテゴリを削除する
    service.deleteCategory(categoryId);

    // 店舗メインページ
    return "redirect:/shop/{shopId}/main";
  }

}
