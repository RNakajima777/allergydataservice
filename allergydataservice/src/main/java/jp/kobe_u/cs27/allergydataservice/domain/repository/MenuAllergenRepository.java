package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuAllergen;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * メニューとアレルゲンの紐付けのリポジトリ
 */
@Repository
public interface MenuAllergenRepository extends CrudRepository<MenuAllergen, Long> {

  List<MenuAllergen> findByMenuId(Long menuId);

  void deleteByMenuId(Long menuId);

}
