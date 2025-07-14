package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/**
 * ユーザ登録・更新フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {

  // ユーザID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String uid;

  // ユーザーネーム
  @NotBlank
  private String username;

  // 性別
  private int sex;

  //生年月日
  @Past
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;
  
}
