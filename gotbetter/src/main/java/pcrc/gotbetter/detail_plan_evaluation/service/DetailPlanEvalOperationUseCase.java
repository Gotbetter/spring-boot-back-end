package pcrc.gotbetter.detail_plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanEvalOperationUseCase {
	DetailPlanEvalReadUseCase.FindDetailPlanEvalResult createDetailPlanEvaluation(DetailPlanEvaluationCommand command);

	DetailPlanEvalReadUseCase.FindDetailPlanEvalResult deleteDetailPlanEvaluation(DetailDislikeDeleteCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanEvaluationCommand {
		private final Long detailPlanId;
		private final Long userId;
		private final Boolean admin;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailDislikeDeleteCommand {
		private final Long detailPlanId;
		private final Long participantId;
		private final Boolean admin;
	}
}
