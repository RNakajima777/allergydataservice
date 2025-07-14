package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.AllergicReactionForm;
import jp.kobe_u.cs27.allergydataservice.application.form.AllergicSymptomForm;
import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicSymptom;
import jp.kobe_u.cs27.allergydataservice.domain.entity.User;
import jp.kobe_u.cs27.allergydataservice.domain.repository.AllergicReactionRepository;
import jp.kobe_u.cs27.allergydataservice.domain.repository.AllergicSymptomRepository;
import jp.kobe_u.cs27.allergydataservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static jp.kobe_u.cs27.allergydataservice.configuration.exception.ErrorCode.*;

/**
 * 体調記録・レビューのロジックを提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class AllergicSymptomService {

  private final UserRepository users;
  private final AllergicSymptomRepository allergicsymptom;
  private final UserService userService;

  //症状を保存する
  public void createSymptom(AllergicReactionForm form, Long reactionid) {

    List<AllergicSymptomForm> symptomForms = form.getSymptomForms();

    if(symptomForms != null){
      for (AllergicSymptomForm symptomForm : symptomForms) {
          // データベースに保存
          allergicsymptom.save(new AllergicSymptom(
              null,
              reactionid,
              symptomForm.getSymptom(),
              symptomForm.getReactionTime(),
              symptomForm.getFeature()
          ));
      }
    }
  }

  //以前の症状を削除し、新しい症状を保存する
  @Transactional
  public void updateSymptom(AllergicReactionForm form, Long reactionid) {

    deleteSymptomByReactionid(reactionid);

    List<AllergicSymptomForm> symptomForms = form.getSymptomForms();

    if(symptomForms != null){
      for (AllergicSymptomForm symptomForm : symptomForms) {
          // データベースに保存
          allergicsymptom.save(new AllergicSymptom(
              null,
              reactionid,
              symptomForm.getSymptom(),
              symptomForm.getReactionTime(),
              symptomForm.getFeature()
          ));
      }
    }
  }

  //指定の反応idを持つ症状を削除する
  @Transactional
  public void deleteSymptomByReactionid(Long reactionid) {
    allergicsymptom.deleteByReactionid(reactionid);  // IDに基づいて削除
  }


  //指定の反応idを持つ症状をリストにして返す
  public List<AllergicSymptom> getSymptoms(Long reactionid) {
    return allergicsymptom.findByReactionid(reactionid);
  }

}
