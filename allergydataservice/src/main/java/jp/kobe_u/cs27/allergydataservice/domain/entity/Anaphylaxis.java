package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Anaphylaxis {

    // アナフィラキシーId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long anaid;

    // アレルギー反応ID
    private Long reactionid;

    // 症状
    private String anasymptom;

    // 発生年月日
    private LocalDate anadate;

    // エピペンの投与
    private int epipen;

}
