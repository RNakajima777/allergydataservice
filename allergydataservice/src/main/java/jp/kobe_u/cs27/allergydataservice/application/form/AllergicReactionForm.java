package jp.kobe_u.cs27.allergydataservice.application.form;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/**
 * アレルゲン反応設定フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllergicReactionForm {

    // アレルギー反応Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionid;

    // ユーザID
    private String uid;

    // アレルゲンID
    private Long allergenid;

    //ユーザーのつくったアレルゲン名
    private String allergenNameByUser;

    // コンタミの可否
    private int contamination;

    // 反応を起こす分量
    private String quantity;

    // アレルゲンの産地
    private String producingArea;

    //反応の症状
    private List<AllergicSymptomForm> symptomForms;

    //生命に関わる反応の経験
    private int anaExperience;

    //生命に関わる反応の可能性
    private int anaRisk;

    //その他
    private String comment;
    
}
