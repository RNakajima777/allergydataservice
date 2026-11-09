package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import java.util.List;

/**
 * 客向けメニュー絞り込みフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuFilterForm {

  // ユーザID
  private String uid;

  // アレルゲンごとの絞り込み条件
  private List<MenuFilterConditionForm> conditions;

}
