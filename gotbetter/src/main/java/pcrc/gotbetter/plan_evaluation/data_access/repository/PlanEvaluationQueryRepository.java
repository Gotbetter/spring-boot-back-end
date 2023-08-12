package pcrc.gotbetter.plan_evaluation.data_access.repository;

import java.util.List;

import pcrc.gotbetter.plan_evaluation.data_access.dto.PlanEvaluationDto;

public interface PlanEvaluationQueryRepository {

	Boolean existsEval(Long planId, Long participantId);

	List<PlanEvaluationDto> findDislikeUsers(Long planId);
}
