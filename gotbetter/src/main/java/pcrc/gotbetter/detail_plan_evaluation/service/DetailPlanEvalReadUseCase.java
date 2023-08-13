package pcrc.gotbetter.detail_plan_evaluation.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan_evaluation.data_access.dto.DetailPlanEvalDto;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;

public interface DetailPlanEvalReadUseCase {

	List<FindDetailDislikeListResult> getDetailDislikeList(DetailDislikeFindQuery query) throws IOException;

	List<FindDetailDislikeListResult> getDetailNotDislikeList(DetailDislikeFindQuery query) throws IOException;

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class DetailDislikeFindQuery {
		private final Long detailPlanId;
	}

	@Getter
	@ToString
	@Builder
	class FindDetailPlanEvalResult {
		private final Long detailPlanId;
		private final String content;
		private final Boolean complete;
		private final Boolean rejected;
		private final Long planId;
		private final Integer detailPlanDislikeCount;
		private final Boolean detailPlanDislikeChecked;

		public static FindDetailPlanEvalResult findByDetailPlanEval(
			DetailPlan detailPlan,
			Integer dislikeCount,
			Boolean checked
		) {
			return FindDetailPlanEvalResult.builder()
				.detailPlanId(detailPlan.getDetailPlanId())
				.content(detailPlan.getContent())
				.complete(detailPlan.getComplete())
				.rejected(detailPlan.getRejected())
				.planId(detailPlan.getPlanId())
				.detailPlanDislikeCount(dislikeCount)
				.detailPlanDislikeChecked(checked)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindDetailDislikeListResult {
		private final Long detailPlanId;
		private final Long planId;
		private final Long userId;
		private final String username;
		private final String profile;
		private final String createdDate;
		// for admin
		private final Long participantId;

		public static FindDetailDislikeListResult findByDetailDislikeList(
			DetailPlanEvalDto detailPlanEvalDto,
			String bytes
		) {
			return FindDetailDislikeListResult.builder()
				.detailPlanId(detailPlanEvalDto.getDetailPlanEval().getDetailPlanEvalId().getDetailPlanId())
				.planId(detailPlanEvalDto.getDetailPlanEval().getDetailPlanEvalId().getPlanId())
				.userId(detailPlanEvalDto.getUser().getUserId())
				.username(detailPlanEvalDto.getUser().getUsername())
				.profile(bytes)
				.createdDate(detailPlanEvalDto.getDetailPlanEval().getCreatedDate().toLocalDate().toString())
				.participantId(detailPlanEvalDto.getDetailPlanEval().getDetailPlanEvalId().getParticipantId())
				.build();
		}

		public static FindDetailDislikeListResult findByDetailNotDislikeList(
			ParticipantDto participantDto,
			String bytes,
			Long planId,
			Long detailPlanId
		) {
			return FindDetailDislikeListResult.builder()
				.detailPlanId(detailPlanId)
				.planId(planId)
				.userId(participantDto.getUser().getUserId())
				.username(participantDto.getUser().getUsername())
				.profile(bytes)
				.build();
		}
	}
}
