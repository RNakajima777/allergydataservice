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

public class AllergyListDTO {

    // アレルゲン反応Id
    private Long reactionid;

    // アレルゲン名
    private String allergenName;

    //アレルゲンの種類(食品1、吸引2、接触3)
    private int allergenType;

}