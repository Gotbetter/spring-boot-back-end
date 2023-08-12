package pcrc.gotbetter.plan_evaluation.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan_evaluation.data_access.dto.PlanEvaluationDto;

public interface PlanEvaluationReadUseCase {

	FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query);

	List<FindPlanDislikeListResult> getPlanDislikeList(PlanEvaluationFindQuery query);

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

	@Getter
	@ToString
	@Builder
	class FindPlanDislikeListResult {
		private final Long planId;
		private final Long userId;
		private final String username;
		private final String profile;
		private final String createdDate;

		public static FindPlanDislikeListResult findByPlanDislikeList(PlanEvaluationDto planEvaluationDto) {
			return FindPlanDislikeListResult.builder()
				.planId(planEvaluationDto.getPlanEvaluation().getPlanEvaluationId().getPlanId())
				.userId(planEvaluationDto.getUser().getUserId())
				.username(planEvaluationDto.getUser().getUsername())
				.profile(planEvaluationDto.getUser().getProfile())
				.createdDate(planEvaluationDto.getPlanEvaluation().getCreatedDate().toLocalDate().toString())
				.build();
		}
	}
}
