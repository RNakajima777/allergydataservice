package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AllergicSymptom {

    // アナフィラキシーId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long symptomid;

    // アレルギー反応Id
    private Long reactionid;

    //症状
    private String symptom;

    // 反応時間
    private int reactionTime;

    //特徴
    private String feature;
    
}
