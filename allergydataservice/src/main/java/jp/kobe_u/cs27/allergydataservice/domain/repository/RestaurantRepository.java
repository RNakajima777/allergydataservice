package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Restaurant;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {

    List<Restaurant> findByUidOrderByCreatedAtDesc(String uid);

    Restaurant findByRestaurantid(Long restaurantid);

}
