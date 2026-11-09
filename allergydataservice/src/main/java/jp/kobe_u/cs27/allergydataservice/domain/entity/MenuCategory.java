package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

/**
 * 店舗が作成するメニューカテゴリ(ランチ、デザート等)を表すエンティティ
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {

  // カテゴリId
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long categoryId;

  // 店舗ログインID
  private String shopId;

  // カテゴリ名
  private String categoryName;

}
