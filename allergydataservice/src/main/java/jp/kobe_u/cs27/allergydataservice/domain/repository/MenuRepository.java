package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.Menu;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * メニューのリポジトリ
 */
@Repository
public interface MenuRepository extends CrudRepository<Menu, Long> {

  List<Menu> findByCategoryIdOrderByMenuIdAsc(Long categoryId);

}
