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
public class RestaurantForm {

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
    
}


