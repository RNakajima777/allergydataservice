package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * メニューカテゴリ登録フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryForm {

  // カテゴリ名
  @NotBlank
  @Size(max = 20)
  private String categoryName;

}
