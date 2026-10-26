package M2fst.Guide_Michelin.repository;

import M2fst.Guide_Michelin.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<M2fst.Guide_Michelin.entity.EvaluationEntity, Integer> {
    List<EvaluationEntity> findByAuthor(String author);
}
