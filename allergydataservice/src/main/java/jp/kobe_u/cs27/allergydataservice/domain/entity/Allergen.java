package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Allergen {

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