package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AllergicReaction {

    // アレルギー反応Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionid;

    // ユーザID
    private String uid;

    // アレルゲンID
    private Long allergenid;

    //アレルゲン名
    private String allergenNameByUser;

    // コンタミの可否
    private int contamination;

    // 反応を起こす分量
    private String quantity;

    // アレルゲンの産地
    private String producingArea;

    //生命に関わる反応の経験
    private int anaExperience;

    //生命に関わる反応の可能性
    private int anaRisk;

    //その他
    private String comment;
    
}
