package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.MenuFilterConditionForm;
import jp.kobe_u.cs27.allergydataservice.application.form.MenuFilterForm;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 客向けメニュー絞り込み画面に関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class MenuFilterService {

  private final AllergicReactionService allergicReactionService;
  private final MenuAllergenService menuAllergenService;
  private final AllergenService allergenService;

  // グループアレルゲン名 → 主要29品目のうち対応するアレルゲン名の一覧
  // 空リストのグループ(ウリ科/キク科/セリ科/ナス科)は29品目に対応するものが無いため、絞り込み条件には展開しない
  private static final Map<String, List<String>> GROUP_ALLERGEN_MEMBER_NAMES = Map.ofEntries(
      Map.entry("麦類", List.of("小麦")),
      Map.entry("豆類", List.of("ピーナッツ", "大豆")),
      Map.entry("ナッツ類", List.of("クルミ", "カシューナッツ", "アーモンド", "ピスタチオ", "マカダミアナッツ")),
      Map.entry("肉類", List.of("牛肉", "豚肉", "鶏肉", "ゼラチン")),
      Map.entry("魚類", List.of("サケ", "サバ")),
      Map.entry("魚卵類", List.of("イクラ")),
      Map.entry("甲殻類", List.of("エビ", "カニ")),
      Map.entry("軟体類", List.of("イカ")),
      Map.entry("貝類", List.of("アワビ")),
      Map.entry("バラ科", List.of("リンゴ", "モモ", "アーモンド")),
      Map.entry("ウリ科", List.of()),
      Map.entry("キク科", List.of()),
      Map.entry("セリ科", List.of()),
      Map.entry("ナス科", List.of()),
      Map.entry("柑橘類", List.of("オレンジ")));

  /**
   * 指定したユーザの登録アレルギーから、絞り込み画面の既定の絞り込み条件を組み立てる
   * ユーザーの登録データ自体は一切変更せず、画面表示用に以下のルールで変換する:
   * - allergenidがnull(自由記述で登録されたもの)は対象外とする(将来、別途注意喚起で対応予定)
   * - グループアレルゲン(allergenGroup=true)は、GROUP_ALLERGEN_MEMBER_NAMESに従って
   *   29品目側の対応するアレルゲンに展開する(対応が無いグループは対象外)
   * - 上記以外(29品目に含まれる個別アレルゲン)はそのまま1件の条件にする。29品目に含まれない個別アレルゲンは対象外
   * contamination(0=要確認,1=OK,2=NG,9=不明)のうち9は0(要確認)に正規化する
   *
   * @param uid ユーザID
   * @return 絞り込み条件のリスト
   */
  public List<MenuFilterConditionForm> buildDefaultConditions(String uid) {

    Set<Long> trackedIds = menuAllergenService.getMandatoryAndRecommendedAllergenIds();
    List<MenuFilterConditionForm> conditions = new ArrayList<>();

    for (AllergicReaction reaction : allergicReactionService.getReactionsByUid(uid)) {

      if (reaction.getAllergenid() == null) {
        // 自由記述で登録されたアレルゲンは対象外(将来、別途注意喚起で対応予定)
        continue;
      }

      Allergen allergen = allergenService.getAllergenByAllergenid(reaction.getAllergenid());
      int tolerance = (reaction.getContamination() == 9) ? 0 : reaction.getContamination();

      if (allergen.isAllergenGroup()) {
        List<String> memberNames = GROUP_ALLERGEN_MEMBER_NAMES.get(allergen.getAllergenName());
        if (memberNames == null || memberNames.isEmpty()) {
          // 29品目に対応するものが無いグループ、または対応表に無いグループは対象外
          continue;
        }
        for (Allergen member : allergenService.getSpecialAllergen(memberNames)) {
          conditions.add(new MenuFilterConditionForm(
              member.getAllergenid(),
              member.getAllergenName(),
              tolerance,
              true));
        }
        continue;
      }

      if (trackedIds.contains(reaction.getAllergenid())) {
        conditions.add(new MenuFilterConditionForm(
            reaction.getAllergenid(),
            allergen.getAllergenName(),
            tolerance,
            true));
      }
    }

    return conditions;
  }

  /**
   * 絞り込み画面に表示する主要29品目すべての絞り込み条件を組み立てる
   * 登録済みアレルギーから導かれる既定条件(buildDefaultConditions)をselected=trueとして重ね合わせ、
   * それ以外の品目はselected=false・tolerance=0(要確認)の状態で返す
   * (客が「条件を変更する」で任意の品目を追加・除外できるようにするため、常に29品目全件を返す)
   *
   * @param uid ユーザID
   * @return 主要29品目すべての絞り込み条件のリスト(特定原材料→それに準ずるものの順)
   */
  public List<MenuFilterConditionForm> buildAllConditions(String uid) {

    Map<Long, Integer> toleranceByAllergenid = new LinkedHashMap<>();
    for (MenuFilterConditionForm condition : buildDefaultConditions(uid)) {
      toleranceByAllergenid.put(condition.getAllergenid(), condition.getTolerance());
    }

    List<MenuFilterConditionForm> conditions = new ArrayList<>();
    for (Allergen allergen : menuAllergenService.getMandatoryAndRecommendedAllergens()) {

      Integer tolerance = toleranceByAllergenid.get(allergen.getAllergenid());

      conditions.add(new MenuFilterConditionForm(
          allergen.getAllergenid(),
          allergen.getAllergenName(),
          tolerance != null ? tolerance : 0,
          tolerance != null));
    }

    return conditions;
  }

  /**
   * 絞り込みフォームから、アレルゲンidごとのコンタミ許容度のMapを組み立てる
   * 選択されていない(selected=false)品目は絞り込み条件に含めない
   *
   * @param form MenuFilterForm
   * @return アレルゲンidごとのコンタミ許容度のMap
   */
  public Map<Long, Integer> resolveToleranceMap(MenuFilterForm form) {

    Map<Long, Integer> map = new LinkedHashMap<>();

    if (form.getConditions() == null) {
      return map;
    }

    for (MenuFilterConditionForm condition : form.getConditions()) {
      if (!condition.isSelected()) {
        continue;
      }
      map.put(condition.getAllergenid(), condition.getTolerance());
    }

    return map;
  }

  /**
   * アレルゲンidごとのコンタミ許容度のMapから、メニュー一覧画面上部に表示する簡素な条件表示文を組み立てる
   * (例: 「卵：コンタミOK」「乳：コンタミ要確認」)
   *
   * @param toleranceByAllergenid アレルゲンidごとのコンタミ許容度のMap
   * @return 条件表示文のリスト
   */
  public List<String> buildConditionSummaries(Map<Long, Integer> toleranceByAllergenid) {

    List<String> summaries = new ArrayList<>();

    for (Map.Entry<Long, Integer> entry : toleranceByAllergenid.entrySet()) {
      Allergen allergen = allergenService.getAllergenByAllergenid(entry.getKey());
      summaries.add(allergen.getAllergenName() + "：" + toleranceLabel(entry.getValue()));
    }

    return summaries;
  }

  private String toleranceLabel(int tolerance) {
    switch (tolerance) {
      case 1:
        return "コンタミOK";
      case 2:
        return "コンタミNG";
      default:
        return "コンタミ要確認";
    }
  }

}
