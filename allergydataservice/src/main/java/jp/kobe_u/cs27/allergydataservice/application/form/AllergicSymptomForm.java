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
public class AllergicSymptomForm {

    // 症状Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long symptomid;

    // アレルギー反応Id
    private Long reactionid;

    //症状
    private String symptom;

    // 反応時間
    private int reactionTime;

    //反応に付随する特徴
    private String feature;

}


