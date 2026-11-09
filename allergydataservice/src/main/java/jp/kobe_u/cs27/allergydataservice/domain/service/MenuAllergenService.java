package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuAllergenSelectionForm;
import jp.kobe_u.cs27.allergydataservice.application.form.MenuForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuAllergen;
import jp.kobe_u.cs27.allergydataservice.domain.repository.MenuAllergenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * メニューとアレルゲンの紐付けに関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class MenuAllergenService {

  private final MenuAllergenRepository menuAllergens;
  private final AllergenService allergenService;

  // 特定原材料(既存の客側アレルゲン登録画面と同じ名称リストを使用する)
  private static final List<String> MANDATORY_ALLERGEN_NAMES = Arrays.asList(
      "卵", "牛乳", "小麦", "クルミ", "ピーナッツ", "エビ", "カニ", "そば", "カシューナッツ");

  // 特定原材料に準ずるもの(既存の客側アレルゲン登録画面と同じ名称リストを使用する)
  private static final List<String> RECOMMENDED_ALLERGEN_NAMES = Arrays.asList(
      "アーモンド", "ピスタチオ", "マカダミアナッツ", "ごま", "大豆", "山芋",
      "アワビ", "イカ", "イクラ", "サケ", "サバ", "牛肉", "豚肉", "鶏肉",
      "オレンジ", "キウイ", "リンゴ", "バナナ", "モモ", "ゼラチン");

  /**
   * 特定原材料の件数を返す
   * 画面側で特定原材料とそれに準ずるものの見出しを切り替える位置に使う
   *
   * @return 特定原材料の件数
   */
  public int getMandatoryAllergenCount() {
    return MANDATORY_ALLERGEN_NAMES.size();
  }

  /**
   * 主要29品目(特定原材料9+それに準ずるもの20)のallergenidの集合を返す
   * 客の登録アレルギーが店舗側メニューの判定対象かどうかを確認する際に使う
   *
   * @return 主要29品目のallergenidの集合
   */
  public Set<Long> getMandatoryAndRecommendedAllergenIds() {

    Set<Long> ids = new HashSet<>();

    for (Allergen allergen : allergenService.getSpecialAllergen(MANDATORY_ALLERGEN_NAMES)) {
      ids.add(allergen.getAllergenid());
    }
    for (Allergen allergen : allergenService.getSpecialAllergen(RECOMMENDED_ALLERGEN_NAMES)) {
      ids.add(allergen.getAllergenid());
    }

    return ids;
  }

  /**
   * 主要29品目(特定原材料9+それに準ずるもの20)を、特定原材料→それに準ずるものの順で返す
   *
   * @return 主要29品目のアレルゲンのリスト
   */
  public List<Allergen> getMandatoryAndRecommendedAllergens() {

    List<Allergen> allergens = new ArrayList<>();
    allergens.addAll(allergenService.getSpecialAllergen(MANDATORY_ALLERGEN_NAMES));
    allergens.addAll(allergenService.getSpecialAllergen(RECOMMENDED_ALLERGEN_NAMES));

    return allergens;
  }

  /**
   * 主要品目分の選択フォームを、特定原材料→それに準ずるものの順で組み立てる
   * すべて初期値(0: 未評価)の状態で返す
   *
   * @return 主要品目分の選択フォームのリスト
   */
  public List<MenuAllergenSelectionForm> buildEmptySelectionForms() {

    List<MenuAllergenSelectionForm> forms = new ArrayList<>();

    for (Allergen allergen : allergenService.getSpecialAllergen(MANDATORY_ALLERGEN_NAMES)) {
      forms.add(new MenuAllergenSelectionForm(
          allergen.getAllergenid(),
          allergen.getAllergenName(),
          0));
    }

    for (Allergen allergen : allergenService.getSpecialAllergen(RECOMMENDED_ALLERGEN_NAMES)) {
      forms.add(new MenuAllergenSelectionForm(
          allergen.getAllergenid(),
          allergen.getAllergenName(),
          0));
    }

    return forms;
  }

  /**
   * 主要28品目分の選択フォームを組み立て、指定したメニューに既に保存されている
   * 含有・コンタミ状況で上書きする(閲覧・確認画面向け)
   *
   * @param menuId メニューId
   * @return 登録済みの状況を反映した選択フォームのリスト
   */
  public List<MenuAllergenSelectionForm> buildSelectionFormsForMenu(Long menuId) {

    List<MenuAllergenSelectionForm> forms = buildEmptySelectionForms();

    Map<Long, Integer> presenceByAllergenid = new HashMap<>();
    for (MenuAllergen menuAllergen : getMenuAllergensByMenu(menuId)) {
      presenceByAllergenid.put(menuAllergen.getAllergenid(), menuAllergen.getPresenceType());
    }

    for (MenuAllergenSelectionForm form : forms) {
      Integer presenceType = presenceByAllergenid.get(form.getAllergenid());
      if (presenceType != null) {
        form.setPresenceType(presenceType);
      }
    }

    return forms;
  }

  /**
   * 特定原材料(必須9品目)のうち、含有の有無(有/無)が未回答(未評価)のままの品目名を返す
   * 「特定原材料9品目のみ入力する」表示中(mandatoryOnly)は、それに準ずるもの20品目は対象外とする
   *
   * @param form          MenuForm
   * @param mandatoryOnly 特定原材料9品目のみ表示中かどうか
   * @return 含有が未回答のままの品目名一覧(すべて回答済みであれば空リスト)
   */
  public List<String> getUnansweredContainsAllergenNames(MenuForm form, boolean mandatoryOnly) {

    List<MenuAllergenSelectionForm> allergenForms = form.getAllergenForms();

    if (allergenForms == null) {
      return new ArrayList<>();
    }

    int checkedCount = mandatoryOnly ? getMandatoryAllergenCount() : allergenForms.size();

    return allergenForms
        .subList(0, Math.min(checkedCount, allergenForms.size()))
        .stream()
        .filter(allergenForm -> allergenForm.getPresenceType() == 0)
        .map(MenuAllergenSelectionForm::getAllergenName)
        .collect(Collectors.toList());
  }

  /**
   * 選択されたアレルゲンの含有・コンタミ状況を登録する
   * 「未評価」のまま(0)の行は登録しない
   *
   * @param form   MenuForm
   * @param menuId メニューId
   */
  public void createMenuAllergens(MenuForm form, Long menuId) {

    List<MenuAllergenSelectionForm> allergenForms = form.getAllergenForms();

    if (allergenForms == null) {
      return;
    }

    for (MenuAllergenSelectionForm allergenForm : allergenForms) {
      if (allergenForm.getPresenceType() == 0 || allergenForm.getAllergenid() == null) {
        continue;
      }

      menuAllergens.save(new MenuAllergen(
          null,
          menuId,
          allergenForm.getAllergenid(),
          allergenForm.getPresenceType()));
    }
  }

  /**
   * 指定したメニューのアレルゲン紐付けの一覧を取得する
   *
   * @param menuId メニューId
   * @return アレルゲン紐付けの一覧
   */
  public List<MenuAllergen> getMenuAllergensByMenu(Long menuId) {

    return menuAllergens.findByMenuId(menuId);
  }

  /**
   * 指定したメニューのアレルゲン紐付けをすべて削除する
   *
   * @param menuId メニューId
   */
  @Transactional
  public void deleteByMenu(Long menuId) {

    menuAllergens.deleteByMenuId(menuId);
  }

}
