package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;


import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface AllergicReactionRepository extends CrudRepository<AllergicReaction, Long> {
    //@Query("SELECT reaction FROM AllergicReaction reaction JOIN Allergen a ON reaction.allergenid = a.allergenid " +
    //       "WHERE reaction.uid = :uid AND a.allergenType = 1")
    //List<AllergicReaction> findByUidAndFoodAllergen(@Param("uid") String uid); // 引数をString型に変更

    List<AllergicReaction> findByUid(String uid);

    AllergicReaction findByReactionid(Long reactionid);

    AllergicReaction findByUidAndAllergenid(String uid, Long allergenid);
    
    AllergicReaction findByUidAndAllergenNameByUser(String uid, String allergenName);

}
