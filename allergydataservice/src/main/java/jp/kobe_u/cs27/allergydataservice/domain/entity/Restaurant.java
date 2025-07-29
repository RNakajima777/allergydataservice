package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Restaurant {

    // 対応Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantid;

    // ユーザID
    private String uid;

    // お店の名前
    private String restaurantName;

    //お店の場所
    private String restaurantPlace;

    //メモ
    private String memo;

    // 作成日時
    private LocalDateTime createdAt;
    
}
