package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

/**
 * メニュー登録画面における原産地情報1件分のフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuOriginForm {

  // 食材
  private String ingredientName;

  // 原産地
  private String producingArea;

}
