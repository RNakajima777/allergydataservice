package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

/**
 * 客向けメニュー絞り込み画面における、登録済みアレルゲン1件分の絞り込み条件を表すフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuFilterConditionForm {

  // アレルゲンId(店舗側の主要29品目マスタを参照)
  private Long allergenid;

  // アレルゲン名(画面表示用)
  private String allergenName;

  // 絞り込み条件(コンタミ許容度): 0=要確認, 1=OK, 2=NG
  // 登録済みのcontaminationを正規化した値が初期値。「条件を変更する」チェックを入れて
  // ラジオを操作した場合のみこの値がJSで書き換わる(ロック中はラジオがdisabledのため常に初期値のまま送信される)
  private int tolerance;

  // この品目を絞り込み条件に含めるかどうか
  // 登録済みアレルギー(グループ展開後)から導かれた品目はtrueが初期値。「条件を変更する」チェックを入れた上で
  // チェックボックスを操作した場合のみJSで書き換わる(ロック中はチェックボックスがdisabledのため常に初期値のまま送信される)
  private boolean selected;

}
