package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.MenuOrigin;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * メニューの原産地情報のリポジトリ
 */
@Repository
public interface MenuOriginRepository extends CrudRepository<MenuOrigin, Long> {

  List<MenuOrigin> findByMenuId(Long menuId);

  void deleteByMenuId(Long menuId);

}
