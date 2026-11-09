package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;
import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuAllergen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客の登録アレルギー(コンタミ許容度)とメニューの含有・コンタミ状況を突き合わせ、
 * メニューが食べられるかどうかを判定する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class MenuJudgementService {

  private final MenuAllergenService menuAllergenService;

  // 判定結果: 0=OK(食べられる), 1=要確認(△), 2=NG(食べられない)
  public static final int OK = 0;
  public static final int CAUTION = 1;
  public static final int NG = 2;

  /**
   * 指定したメニュー1件について、客の絞り込み条件(アレルゲンごとのコンタミ許容度)を踏まえた判定を行う
   * ループは客の絞り込み条件を起点にする(メニュー側の保存行を起点にすると、未評価のまま
   * 保存されていない品目がループから漏れて誤ってOK判定になってしまうため)
   *
   * @param menuId               メニューId
   * @param toleranceByAllergenid アレルゲンidごとのコンタミ許容度(0=要確認,1=OK,2=NG)
   * @return 判定結果(OK/CAUTION/NG)
   */
  public int judgeMenu(Long menuId, Map<Long, Integer> toleranceByAllergenid) {

    if (toleranceByAllergenid.isEmpty()) {
      return OK;
    }

    Map<Long, Integer> presenceByAllergenid = menuAllergenService
        .getMenuAllergensByMenu(menuId)
        .stream()
        .collect(Collectors.toMap(MenuAllergen::getAllergenid, MenuAllergen::getPresenceType));

    int worst = OK;
    for (Map.Entry<Long, Integer> entry : toleranceByAllergenid.entrySet()) {
      int presenceType = presenceByAllergenid.getOrDefault(entry.getKey(), 0);
      worst = Math.max(worst, judgeSingle(presenceType, entry.getValue()));
    }

    return worst;
  }

  /**
   * 複数のメニューをまとめて判定する
   *
   * @param menus                 メニューの一覧
   * @param toleranceByAllergenid アレルゲンidごとのコンタミ許容度
   * @return メニューId→判定結果のMap
   */
  public Map<Long, Integer> judgeMenus(List<Menu> menus, Map<Long, Integer> toleranceByAllergenid) {

    Map<Long, Integer> result = new LinkedHashMap<>();

    for (Menu menu : menus) {
      result.put(menu.getMenuId(), judgeMenu(menu.getMenuId(), toleranceByAllergenid));
    }

    return result;
  }

  /**
   * 判定結果を画面表示用の記号に変換する
   *
   * @param code 判定結果(OK/CAUTION/NG)
   * @return ○(OK) / △(要確認) / ×(NG)
   */
  public String toMark(int code) {
    switch (code) {
      case OK:
        return "○";
      case CAUTION:
        return "△";
      default:
        return "×";
    }
  }

  /**
   * アレルゲン1件分の含有・コンタミ状況と客のコンタミ許容度から判定する
   *
   * @param presenceType メニュー側の含有・コンタミ状況(0〜4)
   * @param tolerance    客のコンタミ許容度(0=要確認,1=OK,2=NG)
   * @return 判定結果(OK/CAUTION/NG)
   */
  private int judgeSingle(int presenceType, int tolerance) {

    if (presenceType == 1) {
      // 含有 → 無条件でNG
      return NG;
    }
    if (presenceType == 4) {
      // コンタミ無 → 無条件でOK
      return OK;
    }
    if (presenceType == 0) {
      // 未評価(本来発生しないはずだが念のため) → 客の許容度に関わらず常に要確認。OKには絶対にしない
      return CAUTION;
    }

    // ここに来るのはpresenceType 2(コンタミ有)/3(コンタミ不明)
    switch (tolerance) {
      case 1:
        return OK;
      case 2:
        return NG;
      default:
        return CAUTION;
    }
  }

}
