package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicReaction;
import jp.kobe_u.cs27.allergydataservice.domain.entity.AllergicSymptom;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface AllergicSymptomRepository extends CrudRepository<AllergicSymptom, Long> {
    List<AllergicSymptom> findByReactionid(Long reactionid);
    
    void deleteByReactionid(Long reactionid);
}
