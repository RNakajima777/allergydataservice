package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザのリポジトリ
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {

}
