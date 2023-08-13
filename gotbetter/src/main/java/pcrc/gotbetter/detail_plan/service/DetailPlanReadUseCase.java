package pcrc.gotbetter.detail_plan.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

public interface DetailPlanReadUseCase {

	List<FindDetailPlanResult> getDetailPlans(DetailPlanFindQuery query);

	FindDetailPlanResult getDetailPlan(DetailPlanOneFindQuery query);

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class DetailPlanFindQuery {
		private final Long planId;
		private final Boolean admin;
	}

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class DetailPlanOneFindQuery {
		private final Long planId;
		private final Long detailPlanId;
	}

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
		// for admin
		private final String createdDate;
		private final String updatedDate;

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
				.createdDate(detailPlan.getCreatedDate().toLocalDate().toString())
				.updatedDate(detailPlan.getUpdatedDate().toLocalDate().toString())
				.build();
		}
	}
}
