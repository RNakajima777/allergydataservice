package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

/**
 * メニュー登録画面における主要品目1つ分の選択状態を表すフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuAllergenSelectionForm {

  // アレルゲンId(既存のAllergenマスタを参照)
  private Long allergenid;

  // アレルゲン名(確認画面での表示・引き継ぎ用)
  private String allergenName;

  // 0: 未評価(未入力), 1: 含有, 2: 含有無・コンタミ有, 3: 含有無・コンタミ不明, 4: 含有無・コンタミ無
  private int presenceType;

  /**
   * 画面表示用の記号に変換する
   *
   * @return ●(含有) / △(コンタミ有) / ？(コンタミ不明) / ー(含有・コンタミともになし) / 未評価
   */
  public String getDisplayMark() {
    switch (presenceType) {
      case 1:
        return "●";
      case 2:
        return "△";
      case 3:
        return "？";
      case 4:
        return "ー";
      default:
        return "未評価";
    }
  }

}
