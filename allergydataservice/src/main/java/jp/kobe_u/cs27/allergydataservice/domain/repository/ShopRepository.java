package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.Shop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 店舗のリポジトリ
 */
@Repository
public interface ShopRepository extends CrudRepository<Shop, String> {

}
