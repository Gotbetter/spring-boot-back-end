package pcrc.gotbetter.plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface PlanEvaluationOperationUseCase {
	PlanEvaluationReadUseCase.FindPlanEvaluationResult createPlanEvaluation(PlanEvaluationCommand command);

	PlanEvaluationReadUseCase.FindPlanEvaluationResult createPlanEvaluationAdmin(PlanEvaluationAdminCommand command);

	void deletePlanEvaluation(PlanEvaluationCommand command);

	void deletePlanEvaluationAdmin(PlanEvaluationDeleteAdminCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class PlanEvaluationCommand {
		private final Long planId;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class PlanEvaluationAdminCommand {
		private final Long planId;
		private final Long userId;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class PlanEvaluationDeleteAdminCommand {
		private final Long planId;
		private final Long participantId;
	}
}
