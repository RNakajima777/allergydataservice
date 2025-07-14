package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.allergydataservice.application.form.UidForm;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jp.kobe_u.cs27.allergydataservice.application.form.AllergenForm;
import jp.kobe_u.cs27.allergydataservice.domain.service.AllergenService;
import java.util.List;

//管理者としてアレルゲンの設定を行う部分のコントローラー
@Controller
@RequiredArgsConstructor
public class ManagerController {

  private final AllergenService allergenService;


  /**
   * アレルゲン設定ページに移動する
   *
   * @param model
   * @param attributes
   * @param form  ユーザID
   * @return アレルゲン設定ページ
   */
  @GetMapping("/manager/enter")
  public String addAllergen(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UidForm form,
      BindingResult bindingResult) {

    model.addAttribute(new AllergenForm());
    return "managerAllergenRegister";
  }

  /**
   * アレルゲンを設定する
   *
   * @param attributes
   * @param form HealthConditionForm
   * @return アレルゲン設定一覧ページ
   */
  @PostMapping("/manager/allergen/add")
  public String inputAllergen(
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          AllergenForm form,
      BindingResult bindingResult) {

    // スコアにエラーが含まれていた場合
    if(bindingResult.hasErrors()){
      // エラーフラグをオンにする
      attributes.addFlashAttribute("isHealthConditionFormError", true);

      // アレルゲン入力ページ
      return "redirect:/managerlist";
    }

    final String allergenName = form.getAllergenName();

    // ユーザが既に存在するか確認する
    if (allergenService.existsAllergen(allergenName)) {
      
      // ユーザ登録ページに戻る
      return "redirect:/managerlist";
    }

    // アレルゲンを記録する
    allergenService.record(form);

    // アレルゲン記録ページ
    return "redirect:/managerlist";
  }

  //アレルゲン設定リストを表示する
  @GetMapping("/managerlist")
  public String showAllergenList(Model model){
    model.addAttribute("allergenList", allergenService.getAllAllergen());
    return "managerAllergenList";
  }

  //アレルゲン編集画面に移動する
  @GetMapping("/manager/allergen/edit/{allergenid}")
  public String editAllergen(Model model, @PathVariable("allergenid") Long allergenid) {
    model.addAttribute("allergen", allergenService.getAllergenByAllergenid(allergenid));
    model.addAttribute(new AllergenForm());
    return "managerAllergenEditer";
  }

  //編集したアレルゲン設定を保存する
  @PostMapping("/manager/allergen/correct/{allergenid}")
  public String correctAllergen(
    RedirectAttributes attributes,
    @ModelAttribute
    @Validated
        AllergenForm form,
    @PathVariable("allergenid") Long allergenid,
    BindingResult bindingResult) {

    // スコアにエラーが含まれていた場合
    if(bindingResult.hasErrors()){
      // エラーフラグをオンにする
      attributes.addFlashAttribute("isHealthConditionFormError", true);

      // 体調入力ページ
      return "redirect:managerAllergenEditer";
    }

    // 体調を記録する
    allergenService.updateAllergen(allergenid, form);

    // 体調記録ページ
    return "redirect:/managerlist";
  }

  // アレルゲン設定を削除する
  @GetMapping("/manager/allergen/delete/{allergenid}")
  public String deleteAllergen(@PathVariable("allergenid") Long allergenid) {
      allergenService.deleteAllergenByAllergenid(allergenid);  // アイテムを削除
      return "redirect:/managerlist";  // 削除後、一覧ページにリダイレクト
  }
  
}
