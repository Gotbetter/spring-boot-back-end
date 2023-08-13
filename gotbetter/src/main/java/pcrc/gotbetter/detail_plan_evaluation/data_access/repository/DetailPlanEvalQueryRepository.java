package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import java.util.List;

import pcrc.gotbetter.detail_plan_evaluation.data_access.dto.DetailPlanEvalDto;

public interface DetailPlanEvalQueryRepository {

	Boolean existsEval(Long detailPlanId, Long participantId);

	List<DetailPlanEvalDto> findDislikeUsers(Long detailPlanId);
}
