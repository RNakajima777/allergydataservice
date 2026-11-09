package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuCategory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * メニューカテゴリのリポジトリ
 */
@Repository
public interface MenuCategoryRepository extends CrudRepository<MenuCategory, Long> {

  List<MenuCategory> findByShopIdOrderByCategoryIdAsc(String shopId);

}
