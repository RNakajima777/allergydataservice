package jp.kobe_u.cs27.allergydataservice.domain.service;

import jp.kobe_u.cs27.allergydataservice.application.form.RestaurantForm;
import jp.kobe_u.cs27.allergydataservice.configuration.exception.UserValidationException;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Restaurant;
import jp.kobe_u.cs27.allergydataservice.domain.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jp.kobe_u.cs27.allergydataservice.configuration.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 対応メモに関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class RestaurantService {

  private final RestaurantRepository restaurants;

  /**
   * 対応メモを登録する
   *
   * @param form UserForm
   * @return 登録したユーザの情報
   */
  public Restaurant createRestaurant(RestaurantForm form) {

    // ユーザをDBに登録し、登録したユーザの情報を戻り値として返す
    return restaurants.save(new Restaurant(
        null,
        form.getUid(),
        form.getRestaurantName(),
        form.getRestaurantPlace(),
        form.getMemo(),
        LocalDateTime.now()
        ));
  }

  /**
   * 指定したメモがDBに登録済みかどうか確認する
   *
   * @param restaurantid ユーザID
   * @return 指定したユーザが存在するかどうかの真偽値(存在する場合にtrue)
   */
  public boolean existsRestaurant(Long restaurantid) {

    // 指定したユーザがDBに登録済みか確認し、結果を戻り値として返す
    return restaurants.existsById(restaurantid);
  }

  public List<Restaurant> getRestaurantListByUid(String uid){
    List<Restaurant> returnList = restaurants.findByUidOrderByCreatedAtDesc(uid);
    return returnList;
  } 

  //指定のレストランidを持つレストランメモを返す
  public Restaurant getRestaurantByRestaurantid(Long restaurantid) {
    return restaurants.findByRestaurantid(restaurantid);
  }

  /**
   * メモの情報を更新する
   *
   * @param form RestaurantForm
   * @return 更新したユーザの情報
   */
  public Restaurant updateRestaurant(RestaurantForm form, Long restaurantid) {

    Restaurant restaurant = getRestaurantByRestaurantid(restaurantid);
    restaurant.setRestaurantName(form.getRestaurantName());
    restaurant.setRestaurantPlace(form.getRestaurantPlace());
    restaurant.setMemo(form.getMemo());
  
    return restaurants.save(restaurant);

  }

  /**
   * メモを削除する
   * 処理に失敗した場合、このメソッド中のDB操作はすべてロールバックされる
   *
   * @param uid ユーザID
   */
  @Transactional
  public void deleteRestaurantByRestaurantid(Long restaurantid) {

    // ユーザが存在しない場合、例外を投げる
    if (!restaurants.existsById(restaurantid)) {
      throw new UserValidationException(
          RESTAURANT_DOES_NOT_EXIST,
          "delete the restaurant",
          "the restaurant does not exist"
          );
    }

    // メモを削除する
    restaurants.deleteById(restaurantid);

  }

}
