package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 店舗登録フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopForm {

  // 店舗ログインID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String shopId;

  // 店舗名
  @NotBlank
  private String shopName;

  // パスワード
  @Size(min = 8, max = 100)
  @NotBlank
  private String password;

  // パスワード確認用
  @NotBlank
  private String passwordConfirm;

}
