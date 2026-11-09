package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

/**
 * メニューの原産地情報(食材と原産地の組)を表すエンティティ
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuOrigin {

  // 原産地情報Id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long originId;

  // メニューId
  private Long menuId;

  // 食材
  private String ingredientName;

  // 原産地
  private String producingArea;

}
