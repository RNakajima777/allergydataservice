package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/**
 * アレルゲン設定フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllergenForm {

  // アレルゲンId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allergenid;

    //アレルゲンの種類(食品1、吸引2、接触3)
    private int allergenType;

    // アレルゲン名
    private String allergenName;

    // 食品の分類
    private String foodFamily;
  
}


