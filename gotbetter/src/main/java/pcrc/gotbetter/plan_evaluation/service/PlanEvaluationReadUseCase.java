package pcrc.gotbetter.plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan.data_access.entity.Plan;

public interface PlanEvaluationReadUseCase {

	FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query);

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class PlanEvaluationFindQuery {
		private final Long planId;
	}

	@Getter
	@ToString
	@Builder
	class FindPlanEvaluationResult {
		private final Long planId;
		private final Boolean rejected;
		private final Integer dislikeCount;
		private final Boolean checked; // 사용자가 계획 평가 했는지

		public static FindPlanEvaluationResult findByPlanEvaluation(
			Plan plan,
			Integer dislikeCount,
			Boolean checked
		) {
			return FindPlanEvaluationResult.builder()
				.planId(plan.getPlanId())
				.rejected(plan.getRejected())
				.dislikeCount(dislikeCount)
				.checked(checked)
				.build();
		}
	}
}
