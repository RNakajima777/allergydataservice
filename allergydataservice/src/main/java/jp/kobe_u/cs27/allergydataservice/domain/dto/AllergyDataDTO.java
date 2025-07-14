package jp.kobe_u.cs27.allergydataservice.domain.dto;

import lombok.*;

import jakarta.persistence.*;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AllergyDataDTO {

    // アレルゲン反応Id
    private Long reactionid;

    // アレルゲン名
    private String allergenName;

    // コンタミの可否
    private int contamination;

    //生命に関わる反応の可能性
    private int anaRisk;

    //特記事項
    private String extraNotes;

}