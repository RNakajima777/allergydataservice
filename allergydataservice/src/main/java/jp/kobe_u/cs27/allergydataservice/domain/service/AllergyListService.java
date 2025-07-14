package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.AllergicReactionForm;
import jp.kobe_u.cs27.allergydataservice.application.form.UserForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.AllergenValidationException;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.dto.AllergyDataDTO;
import jp.kobe_u.cs27.allergydataservice.domain.dto.AllergyListDTO;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import jp.kobe_u.cs27.allergydataservice.domain.entity.User;
import jp.kobe_u.cs27.allergydataservice.domain.repository.AllergenRepository;
import jp.kobe_u.cs27.allergydataservice.domain.repository.AllergicReactionRepository;
import jp.kobe_u.cs27.allergydataservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
public class AllergyListService {

  private final AllergicReactionRepository allergy;
  private final UserService userService;
  private final AllergenService allergenService;

  public List<AllergyListDTO> getAllergyListByUid(String uid){
    List<AllergyListDTO> returnList = new ArrayList<>();
    List<AllergicReaction> foodReactions = allergy.findByUid(uid);
    for (AllergicReaction foodReaction : foodReactions) {
      if(foodReaction.getAllergenid() != null) {
        returnList.add(new AllergyListDTO(foodReaction.getReactionid(), allergenService.getAllergenByAllergenid(foodReaction.getAllergenid()).getAllergenName(),1));
      } else {
        returnList.add(new AllergyListDTO(foodReaction.getReactionid(), foodReaction.getAllergenNameByUser(),1));
      }
    }

    //吸引、接触と続く

    return returnList;
  } 

  public List<AllergyDataDTO> getAllergyDataByUid(String uid){
    List<AllergyDataDTO> returnData = new ArrayList<>();
    List<AllergicReaction> foodReactions = allergy.findByUid(uid);
    for (AllergicReaction foodReaction : foodReactions) {
      StringBuilder notes = new StringBuilder();

      if ((!"なし".equals(foodReaction.getQuantity())) | (!"なし".equals(foodReaction.getProducingArea())) | (!"なし".equals(foodReaction.getComment()))) {
        if(foodReaction.getAllergenid() != null) {
          notes.append(allergenService.getAllergenByAllergenid(foodReaction.getAllergenid()).getAllergenName()).append("に関する特記事項").append("\n");
        } else {
          notes.append(foodReaction.getAllergenNameByUser()).append("に関する特記事項").append("\n");
        }
      }

      if (!"なし".equals(foodReaction.getQuantity())) {
          notes.append("分量:").append(foodReaction.getQuantity()).append("\n");
      }
      if (!"なし".equals(foodReaction.getProducingArea())) {
          notes.append("ダメな産地：").append(foodReaction.getProducingArea()).append("\n");
      }
      if (!"なし".equals(foodReaction.getComment())) {
          notes.append("その他：").append(foodReaction.getComment()).append("\n");
      }


      // HTMLで改行表示したい場合は <br> に変換（推奨）
      String formattedNotes = notes.toString().replace("\n", "<br>");

      //DTOにセット 
      if(foodReaction.getAllergenid() != null) {
        returnData.add(new AllergyDataDTO(foodReaction.getReactionid(), allergenService.getAllergenByAllergenid(foodReaction.getAllergenid()).getAllergenName(),foodReaction.getContamination(), foodReaction.getAnaRisk(), formattedNotes));
      } else {
        returnData.add(new AllergyDataDTO(foodReaction.getReactionid(), foodReaction.getAllergenNameByUser(),foodReaction.getContamination(), foodReaction.getAnaRisk(), formattedNotes));
      }
    }

    //吸引、接触と続く

    returnData.sort(
        Comparator.comparingInt((AllergyDataDTO dto) -> {
            switch (dto.getAnaRisk()) {
                case 1: return 0;
                case 9: return 1;
                case 0: return 2;
                case 2: return 3;
                default: return 4; // 想定外
            }
        }).thenComparingInt(dto -> {
            switch (dto.getContamination()) {
                case 2: return 0;
                case 9: return 1;
                case 0: return 2;
                case 1: return 3;
                default: return 4; // 想定外
            }
        })
    );

    return returnData;
  } 
  
}