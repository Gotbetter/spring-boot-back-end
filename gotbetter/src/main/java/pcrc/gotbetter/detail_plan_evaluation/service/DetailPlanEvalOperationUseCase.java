package pcrc.gotbetter.detail_plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanEvalOperationUseCase {
	DetailPlanEvalReadUseCase.FindDetailPlanEvalResult createDetailPlanEvaluation(DetailPlanEvaluationCommand command);

	DetailPlanEvalReadUseCase.FindDetailPlanEvalResult deleteDetailPlanEvaluation(DetailPlanEvaluationCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanEvaluationCommand {
		private final Long detailPlanId;
	}
}
