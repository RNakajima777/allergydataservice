package jp.kobe_u.cs27.allergydataservice.domain.entity;

import lombok.*;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {

  // ユーザID
  @Id
  private String uid;

  // ユーザーネーム
  private String username;

  // 性別
  private int sex;

  //生年月日
  private LocalDate birthday;
  
}
