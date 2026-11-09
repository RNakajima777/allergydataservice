package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

/**
 * 店舗が登録するメニューを表すエンティティ
 * 型は暫定であり、今後の登録画面実装に合わせて変更する可能性がある
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

  // メニューId
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long menuId;

  // 店舗ログインID
  private String shopId;

  // カテゴリId
  private Long categoryId;

  // メニュー名
  private String menuName;

  // メニュー画像のパス(またはURL)
  private String imagePath;

  // 値段
  private int price;

  // 特記事項
  private String comment;

}
