package pcrc.gotbetter.plan_evaluation.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluationId;

@Repository
public interface PlanEvaluationRepository
	extends JpaRepository<PlanEvaluation, PlanEvaluationId>, PlanEvaluationQueryRepository {
	List<PlanEvaluation> findByPlanEvaluationIdPlanId(Long planId);

	void deleteByPlanEvaluationIdPlanId(Long planId);
}
