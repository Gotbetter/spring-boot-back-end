package pcrc.gotbetter.plan_evaluation.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;

import java.util.List;

@Repository
public interface PlanEvaluationRepository extends JpaRepository<PlanEvaluation, Long>, PlanEvaluationRepositoryQueryDSL {
    List<PlanEvaluation> findByPlanId(Long plan_id);
    void deleteByPlanId(Long plan_id);
}
