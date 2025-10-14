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

    // ユーザID
    private String uid;

    // エピペンの有無
    private int epipen;

    // 緊急連絡先
    private String emergency;

    // その他推奨される対応
    private String support;

}
