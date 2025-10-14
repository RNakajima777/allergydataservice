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
public class AnaphylaxisForm {

  // アナフィラキシーId
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long anaid;

  // ユーザID
    private String uid;

    // エピペンの有無
    private int epipen;

    // 緊急連絡先
    private String emergency;

    // その他推奨される対応
    private String support;
  
}


