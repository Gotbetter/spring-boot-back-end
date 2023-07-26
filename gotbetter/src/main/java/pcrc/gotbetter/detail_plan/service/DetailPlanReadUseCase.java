package pcrc.gotbetter.detail_plan.service;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

public interface DetailPlanReadUseCase {

	List<FindDetailPlanResult> getDetailPlans(Long planId);

	@Getter
	@ToString
	@Builder
	class FindDetailPlanResult {
		private final Long detailPlanId;
		private final String content;
		private final Boolean complete;
		private final Boolean rejected;
		private final Long planId;
		private final Integer detailPlanDislikeCount;
		private final Boolean detailPlanDislikeChecked;

		public static FindDetailPlanResult findByDetailPlan(
			DetailPlan detailPlan,
			Integer dislikeCount,
			Boolean checked
		) {
			return FindDetailPlanResult.builder()
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
}
