package jp.kobe_u.cs27.allergydataservice.domain.repository;

import jp.kobe_u.cs27.allergydataservice.domain.entity.Anaphylaxis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnaphylaxisRepository extends JpaRepository<Anaphylaxis, Long> {
    Optional<Anaphylaxis> findByUid(String uid);
}
