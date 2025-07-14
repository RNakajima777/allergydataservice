package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.AllergenForm;
import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;
import jp.kobe_u.cs27.allergydataservice.domain.entity.User;
import jp.kobe_u.cs27.allergydataservice.domain.repository.AllergenRepository;
import jp.kobe_u.cs27.allergydataservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static jp.kobe_u.cs27.allergydataservice.configuration.exception.ErrorCode.*;

/**
 * 体調記録・レビューのロジックを提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class AllergenService {

  private final UserRepository users;
  private final AllergenRepository allergen;
  private final UserService userService;

  /**
   * アレルギー反応を記録する
   *
   * @param form AllergenForm
   * @return 記録したアレルゲンデータ
   */
  public Allergen record(
      AllergenForm form) {

    // アレルゲンの記録をDBに保存し、保存した記録を戻り値として返す
    return allergen.save(new Allergen(
        null,
        form.getAllergenType(),
        form.getAllergenName(),
        form.getFoodFamily()
        ));
  }

  // すべての登録アレルゲンをまとめたリストを返す
  public List<Allergen> getAllAllergen() {
    return allergen.findAll();
  }

  // 入力したアレルゲン名リストに合致する、特定のアレルゲンのみをまとめたリストをアレルゲン名リストの順に返す
  public List<Allergen> getSpecialAllergen(List<String> allergenNames) {
    List<Allergen> allergenList = allergen.findByAllergenNameIn(allergenNames);
    // names の順番でソートする
    allergenList.sort(Comparator.comparingInt(a -> allergenNames.indexOf(a.getAllergenName())));
    return allergenList;
  }

  //入力した食品分類に合致するアレルゲンをリスト化して返す
  public List<Allergen> getAllergenByFoodFamily(String foodFamily) {
    return allergen.findByFoodFamily(foodFamily);
  }

  // アレルゲンIDからアレルゲンを探す
  public Allergen getAllergenByAllergenid(Long allergenid) {
    return allergen.findByAllergenid(allergenid);
  }

  // 該当IDを持つアレルゲンに対し、設定編集フォームに入力された内容を新たに保存する
  public Allergen updateAllergen(Long allergenid, AllergenForm form) {  
    Allergen newAllergen = new Allergen();
    newAllergen = allergen.findByAllergenid(allergenid);    
    newAllergen.setAllergenType(form.getAllergenType());
    newAllergen.setAllergenName(form.getAllergenName());
    newAllergen.setFoodFamily(form.getFoodFamily());

    return allergen.save(newAllergen);
  }

  // 該当IDを持つアレルゲンを削除する
  @Transactional
  public void deleteAllergenByAllergenid(Long allergenid) {
    allergen.deleteById(allergenid);  // IDに基づいて削除
  }

  /**
   * 指定したユーザがDBに登録済みかどうか確認する
   *
   * @param uid ユーザID
   * @return 指定したユーザが存在するかどうかの真偽値(存在する場合にtrue)
   */
  public boolean existsAllergen(String allergenName) {

    // 指定したユーザがDBに登録済みか確認し、結果を戻り値として返す
    return allergen.existsByAllergenName(allergenName);
  }

}
