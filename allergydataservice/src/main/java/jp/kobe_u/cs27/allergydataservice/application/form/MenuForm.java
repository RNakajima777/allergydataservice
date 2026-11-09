package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * メニュー登録フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuForm {

  // カテゴリId
  @NotNull
  private Long categoryId;

  // メニュー名
  @NotBlank
  @Size(max = 30)
  private String menuName;

  // アップロードされたメニュー画像(登録画面でのみ使用)
  private MultipartFile image;

  // 画像の保存先パス(確認画面〜登録処理で使用)
  private String imagePath;

  // 値段
  @NotNull
  @Min(0)
  private Integer price;

  // 主要28品目それぞれの含有・コンタミ状況
  private List<MenuAllergenSelectionForm> allergenForms;

  // 原産地情報(食材と原産地の組)
  private List<MenuOriginForm> originForms;

  // 特記事項
  @Size(max = 300)
  private String comment;

}
