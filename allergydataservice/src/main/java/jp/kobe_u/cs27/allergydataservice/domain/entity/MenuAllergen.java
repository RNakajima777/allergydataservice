package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

/**
 * メニューとアレルゲン(主要品目)の紐付けを表すエンティティ
 * 型は暫定であり、今後の登録画面実装に合わせて変更する可能性がある
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuAllergen {

  // メニューアレルゲンId
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long menuAllergenId;

  // メニューId
  private Long menuId;

  // アレルゲンId(既存のAllergenマスタのうち主要品目のみを想定)
  private Long allergenid;

  // 0: 未評価(未入力), 1: 含有, 2: 含有無・コンタミ有, 3: 含有無・コンタミ不明, 4: 含有無・コンタミ無
  private int presenceType;

}
