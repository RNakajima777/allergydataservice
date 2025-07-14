package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.Allergen;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface AllergenRepository extends CrudRepository<Allergen, Long> {

    List<Allergen> findAll();

    Allergen findByAllergenid(Long allergenid);

    List<Allergen> findByFoodFamily(String foodFamily);

    List<Allergen> findByAllergenNameIn(List<String> allergenNames);
    
    boolean existsByAllergenName(String allergenName);
}
