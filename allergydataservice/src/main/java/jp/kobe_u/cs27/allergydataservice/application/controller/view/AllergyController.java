package jp.kobe_u.cs27.allergydataservice.application.controller.view;

import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.AllergenValidationException;
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

import jakarta.servlet.http.HttpSession;
import jp.kobe_u.cs27.allergydataservice.application.form.AllergenForm;
import jp.kobe_u.cs27.allergydataservice.application.form.AllergicReactionForm;
import jp.kobe_u.cs27.allergydataservice.application.form.AllergicSymptomForm;
import jp.kobe_u.cs27.allergydataservice.domain.dto.AllergyDataDTO;
import jp.kobe_u.cs27.allergydataservice.domain.dto.AllergyListDTO;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicSymptom;
import jp.kobe_u.cs27.allergydataservice.domain.service.AllergenService;
import jp.kobe_u.cs27.allergydataservice.domain.service.AllergicReactionService;
import jp.kobe_u.cs27.allergydataservice.domain.service.AllergicSymptomService;
import jp.kobe_u.cs27.allergydataservice.domain.service.AllergyListService;
import jp.kobe_u.cs27.allergydataservice.application.form.RestaurantForm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AllergyController {

  private final UserService service;
  private final AllergenService allergenService;
  private final AllergicReactionService allergicReactionService;
  private final AllergicSymptomService allergicSymptomService;
  private final AllergyListService allergyListService;

  //アレルギーリストを表示する
  @GetMapping("/allergylist")
  public String showAllergenList(
    Model model,
      RedirectAttributes attributes,
      //@ModelAttribute @Validated UserForm form,
      @RequestParam(value = "uid", required = false) String uid) {
    //String uid = form.getUid();
    model.addAttribute("uid", uid);
    model.addAttribute("username", service.getUser(uid).getUsername());
    
    List<AllergyListDTO> allergy;
    allergy = allergyListService.getAllergyListByUid(uid);
  
    model.addAttribute("reactions", allergy);
    //model.addAttribute("foodAllergy", allergicReactionService.getFoodAllergicReactionByUid(uid));
    return "allergyList";

  }

  //ユーザーのアレルギーリストから、登録するアレルゲンの種類を決める画面に移動する  
  @GetMapping("/allergy/add/allergentype")
  public String showAllergenTypeInputPage(
      Model model,
      RedirectAttributes attributes,
      @RequestParam("uid") String uid) {
    model.addAttribute("uid", uid);

    return "allergenTypeRegister";
  }

  //食物アレルゲンの選択画面に移動する
  @GetMapping("/allergy/add/food/allergen")
  public String showAllergenInputPage(
      Model model,
      RedirectAttributes attributes,
      @RequestParam("uid") String uid,
      @ModelAttribute
      @Validated
          UserForm form,
      BindingResult bindingResult) {

    //model.addAttribute("userForm", form);

    // ユーザIDとニックネームをModelに追加する
    model.addAttribute(
        "uid",
        uid);

    List<String> mainSpecificAllergenicIngredients = Arrays.asList("卵", "牛乳", "小麦", "クルミ", "ピーナッツ", "エビ", "カニ", "そば");
    model.addAttribute("mainSpecialAllergens", allergenService.getSpecialAllergen(mainSpecificAllergenicIngredients));
    
    //List<String> otherSpecificAllergenicIngredients = Arrays.asList("アーモンド", "アワビ", "イカ", "イクラ", "オレンジ", "カシューナッツ", "キウイ", "牛肉", "ごま", "サケ", "サバ", "大豆", "鶏肉", "バナナ", "豚肉", "マカダミアナッツ", "桃", "山芋", "リンゴ", "ゼラチン");
    List<String> otherSpecificAllergenicIngredients = Arrays.asList("アーモンド", "カシューナッツ", "マカダミアナッツ", "ごま", "大豆", "山芋", "アワビ", "イカ", "イクラ", "サケ", "サバ", "牛肉", "豚肉", "鶏肉", "オレンジ", "キウイ", "リンゴ", "バナナ", "モモ", "ゼラチン");
    model.addAttribute("otherSpecialAllergens", allergenService.getSpecialAllergen(otherSpecificAllergenicIngredients));

    model.addAttribute("nikuAllergens", allergenService.getAllergenByFoodFamily("肉類"));
    model.addAttribute("kokumotuAllergens", allergenService.getAllergenByFoodFamily("穀物類"));
    model.addAttribute("koukakuAllergens", allergenService.getAllergenByFoodFamily("甲殻類"));
    model.addAttribute("nattuAllergens", allergenService.getAllergenByFoodFamily("ナッツ類"));
    model.addAttribute("kaiAllergens", allergenService.getAllergenByFoodFamily("貝類"));
    model.addAttribute("sakanaAllergens", allergenService.getAllergenByFoodFamily("魚類"));
    model.addAttribute("nantaiAllergens", allergenService.getAllergenByFoodFamily("軟体類"));
    model.addAttribute("gyoranAllergens", allergenService.getAllergenByFoodFamily("魚卵類"));
    model.addAttribute("kudamonoAllergens", allergenService.getAllergenByFoodFamily("果物類"));
    model.addAttribute("imoAllergens", allergenService.getAllergenByFoodFamily("芋類"));
    model.addAttribute("kinokoAllergens", allergenService.getAllergenByFoodFamily("きのこ類"));
    model.addAttribute("yasaiAllergens", allergenService.getAllergenByFoodFamily("野菜類"));
    
    return "foodAllergenRegister";
  }

  //アレルギー反応の登録画面に移動する
  @GetMapping("/allergy/add/food/allergicreaction")
  public String showFoodAllergicReactionInputPage(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          AllergicReactionForm form,
      BindingResult bindingResult) {
    try {
      if (form.getAllergenid() != null) {
          allergicReactionService.validateAllergenDoesNotExist(form.getUid(), form.getAllergenid());
      } else {
          allergicReactionService.validateAllergenDoesNotExistB(form.getUid(), form.getAllergenNameByUser());
      }
    } catch (AllergenValidationException e) {
        // アレルゲンを登録済みであった場合
        // エラーフラグをオンにする
        attributes.addFlashAttribute("isAllergenAlreadyExistsError", true);
        attributes.addAttribute("uid", form.getUid());  // `uid` をURLに追加
    
        // ユーザ登録ページ
        return "redirect:/allergy/add/food/allergen";
    }

    model.addAttribute("allergicReactionForm", form);

    // ユーザIDとニックネームをModelに追加する
    model.addAttribute(
        "uid",
        form.getUid());
    model.addAttribute(
        "allergenid",
        form.getAllergenid());
    model.addAttribute(
      "allergenNameByUser",
      form.getAllergenNameByUser());

    if (form.getAllergenid() != null) {
      // allergenid が null でない場合
      model.addAttribute(
          "allergenName",
          allergenService.getAllergenByAllergenid(form.getAllergenid()).getAllergenName()
      );
    } else {
        // allergenid が null の場合
        model.addAttribute("allergenName", form.getAllergenNameByUser()); // 適切なデフォルト値を設定
    }

    return "foodAllergyRegister";
  }

  //アレルギー反応の確認画面に移動する
  @GetMapping("/allergy/add/food/confirm")
  public String showFoodAllergyConfirmPage(
      Model model,
      @ModelAttribute
      @Validated
          AllergicReactionForm form) {

    model.addAttribute("allergicReactionForm", form);
    List<AllergicSymptomForm> symptomForms = form.getSymptomForms();

    if (form.getQuantity().isBlank()) {
      form.setQuantity("なし");
    }
    if (form.getProducingArea().isBlank()) {
      form.setProducingArea("なし");
    }
    if (form.getComment().isBlank()) {
      form.setComment("なし");
    }
    if (symptomForms != null) {
      for (AllergicSymptomForm symptomForm : symptomForms) {
        if (symptomForm.getFeature() == null || symptomForm.getFeature().isBlank()) {
          symptomForm.setFeature("なし");
        }
      }
    }

    model.addAttribute("symForms", symptomForms);
    if (form.getAllergenid() != null) {
      // allergenid が null でない場合
      model.addAttribute(
          "allergenName",
          allergenService.getAllergenByAllergenid(form.getAllergenid()).getAllergenName()
      );
    } else {
        // allergenid が null の場合
        model.addAttribute("allergenName", form.getAllergenNameByUser()); // 適切なデフォルト値を設定
    }

    return "foodAllergyConfirmRegistration";
  }

  //アレルゲン反応を登録し、リストに戻る
  @PostMapping("/allergy/food/register")
  public String registerFoodAllergy(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          AllergicReactionForm form,
      BindingResult bindingResult) {

    // ユーザを登録する
    try {
      allergicReactionService.createFoodAllergy(form);
    } catch (AllergenValidationException e) {
      // アレルゲンを登録済みであった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isAllergenAlreadyExistsError",
          true);

      attributes.addAttribute("uid", form.getUid());  // `uid` をURLに追加

      // ユーザ登録ページ
      return "redirect:/allergy/add/food/allergen";
    }

    // ユーザIDとニックネームをModelに登録する
    attributes.addAttribute(
        "uid",
        form.getUid());
    attributes.addAttribute(
        "username",
        service.getUser(form.getUid()).getUsername());
    // アレルゲン記録ページ
    return "redirect:/allergylist";
  }

  //登録した食物アレルギー反応の詳細を表示する
  @GetMapping("/allergy/show/food/{reactionid}")
  public String showFoodAllergyDetailPage(Model model, @PathVariable("reactionid") Long reactionid) {
    AllergicReaction allergy = allergicReactionService.getAllergyByReactionid(reactionid);
    model.addAttribute("allergy", allergy);
    Long allergenid = allergy.getAllergenid();
    if(allergenid != null) {
      model.addAttribute("allergenName", allergenService.getAllergenByAllergenid(allergenid).getAllergenName());
    } else {
      model.addAttribute("allergenName", allergy.getAllergenNameByUser());
    }
    List<AllergicSymptom> symptoms = allergicSymptomService.getSymptoms(reactionid);
    model.addAttribute("symForms", symptoms);
    model.addAttribute(new AllergicReactionForm());
    return "foodAllergyDetailViewer";
  }

  //アレルギーを編集する
  @GetMapping("/allergy/edit/food/{reactionid}")
  public String showAllergyEditPage(
    Model model,
    @PathVariable("reactionid") Long reactionid) {
    AllergicReaction allergy = allergicReactionService.getAllergyByReactionid(reactionid);
    model.addAttribute(new AllergicReactionForm());

    // ユーザIDとニックネームをModelに追加する
    model.addAttribute(
        "uid",
        allergy.getUid());
    model.addAttribute(
        "allergenid",
        allergy.getAllergenid());
    model.addAttribute(
      "allergenNameByUser",
      allergy.getAllergenNameByUser());
    if(allergy.getAllergenid() != null){
      model.addAttribute(
      "allergenName",
      allergenService.getAllergenByAllergenid(allergy.getAllergenid()).getAllergenName());
    } else {
      model.addAttribute(
      "allergenName",
      allergy.getAllergenNameByUser());
    }
    model.addAttribute(
      "reactionid",
      reactionid);
    return "foodAllergyEditer";
  }

  //アレルギー反応編集結果確認画面に移動する
  @GetMapping("/allergy/edit/food/confirm/{reactionid}")
  public String showFoodAllergyEditConfirmPage(
      Model model,
      @PathVariable("reactionid") Long reactionid,
      @ModelAttribute
      @Validated
          AllergicReactionForm form) {

    model.addAttribute("allergicReactionForm", form);
    List<AllergicSymptomForm> symptomForms = form.getSymptomForms();

    if (form.getQuantity().isBlank()) {
      form.setQuantity("なし");
    }
    if (form.getProducingArea().isBlank()) {
      form.setProducingArea("なし");
    }
    if (form.getComment().isBlank()) {
      form.setComment("なし");
    }
    if (symptomForms != null) {
      for (AllergicSymptomForm symptomForm : symptomForms) {
        if (symptomForm.getFeature() == null || symptomForm.getFeature().isBlank()) {
          symptomForm.setFeature("なし");
        }
      }
    }

    model.addAttribute("symForms", symptomForms);
    if(form.getAllergenid() != null){
      model.addAttribute(
      "allergenName",
      allergenService.getAllergenByAllergenid(form.getAllergenid()).getAllergenName());
    } else {
      model.addAttribute(
      "allergenName",
      form.getAllergenNameByUser());
    }
    model.addAttribute(
      "reactionid",
      reactionid);
    
    return "foodAllergyConfirmEdit";
  }

  //編集した情報を更新し、リストに戻る
  @PostMapping("/allergy/food/update/{reactionid}")
  public String updateFoodAllergy(
      Model model,
      @PathVariable("reactionid") Long reactionid,
      @ModelAttribute
      @Validated
          AllergicReactionForm form) {

    // アレルギーを登録する
    allergicReactionService.updateFoodAllergy(form, reactionid);
    
    // アレルゲン記録ページ
    return "redirect:/allergy/show/food/{reactionid}";
  }


  //アレルギーを削除する
  @GetMapping("/allergy/delete/{reactionid}")
  public String deleteAllergy(
      Model model,
      RedirectAttributes attributes,
      @PathVariable("reactionid") Long reactionid){
    String uid = allergicReactionService.getAllergyByReactionid(reactionid).getUid();
    attributes.addAttribute(
        "uid",
        uid);
    allergicReactionService.deleteAllergyByReactionid(reactionid);
    allergicSymptomService.deleteSymptomByReactionid(reactionid);
    return "redirect:/allergylist";  // 削除後、一覧ページにリダイレクト
  }


  @GetMapping("/data/{uid}")
  public String showFoodAllergyDataPage(
    Model model,
    RedirectAttributes attributes,
    @PathVariable("uid") String uid){
      model.addAttribute("uid", uid);
      model.addAttribute("username", service.getUser(uid).getUsername());

      List<AllergyDataDTO> allergy;
      allergy = allergyListService.getAllergyDataByUid(uid);
    
      model.addAttribute("reactions", allergy);
      
      return "allergyDataPage"; // データを表示するHTML
  }

  //第三者向け表示ページで詳細画面に移動
  @GetMapping("/data/show/food/{reactionid}")
  public String showFoodAllergyDetailDataPage(Model model, @PathVariable("reactionid") Long reactionid) {
    AllergicReaction allergy = allergicReactionService.getAllergyByReactionid(reactionid);
    model.addAttribute("allergy", allergy);
    Long allergenid = allergy.getAllergenid();
    if(allergenid != null) {
      model.addAttribute("allergenName", allergenService.getAllergenByAllergenid(allergenid).getAllergenName());
    } else {
      model.addAttribute("allergenName", allergy.getAllergenNameByUser());
    }
    List<AllergicSymptom> symptoms = allergicSymptomService.getSymptoms(reactionid);
    model.addAttribute("symForms", symptoms);
    model.addAttribute(new AllergicReactionForm());
    return "foodAllergyDetailData";
  }
  
}
