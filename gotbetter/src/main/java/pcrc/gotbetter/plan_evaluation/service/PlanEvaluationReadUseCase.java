package pcrc.gotbetter.plan_evaluation.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan_evaluation.data_access.dto.PlanEvaluationDto;

public interface PlanEvaluationReadUseCase {

	FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query);

	List<FindPlanDislikeListResult> getPlanDislikeList(PlanEvaluationFindQuery query) throws IOException;

	List<FindPlanDislikeListResult> getPlanNotDislikeList(PlanEvaluationFindQuery query) throws IOException;

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
		// for admin
		private final Long participantId;

		public static FindPlanDislikeListResult findByPlanDislikeList(
			PlanEvaluationDto planEvaluationDto,
			String bytes
		) {
			return FindPlanDislikeListResult.builder()
				.planId(planEvaluationDto.getPlanEvaluation().getPlanEvaluationId().getPlanId())
				.userId(planEvaluationDto.getUser().getUserId())
				.username(planEvaluationDto.getUser().getUsername())
				.profile(bytes)
				.createdDate(planEvaluationDto.getPlanEvaluation().getCreatedDate().toLocalDate().toString())
				.participantId(planEvaluationDto.getPlanEvaluation().getPlanEvaluationId().getParticipantId())
				.build();
		}

		public static FindPlanDislikeListResult findByPlanNotDislikeList(
			ParticipantDto participantDto,
			String bytes,
			Long planId
		) {
			return FindPlanDislikeListResult.builder()
				.planId(planId)
				.userId(participantDto.getUser().getUserId())
				.username(participantDto.getUser().getUsername())
				.profile(bytes)
				.build();
		}
	}
}
