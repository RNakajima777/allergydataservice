package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * 店舗アカウントを表すエンティティ
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

  // 店舗ログインID
  @Id
  private String shopId;

  // 店舗名
  private String shopName;

  // パスワードのハッシュ値
  private String passwordHash;

  // パスワードのソルト
  private String passwordSalt;

}
