package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 店舗ログインフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopLoginForm {

  // 店舗ログインID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String shopId;

  // パスワード
  @NotBlank
  private String password;

}
