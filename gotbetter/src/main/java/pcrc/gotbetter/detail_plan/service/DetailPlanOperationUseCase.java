package pcrc.gotbetter.detail_plan.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanOperationUseCase {

	DetailPlanReadUseCase.FindDetailPlanResult createDetailPlan(DetailPlanCreateCommand command);

	DetailPlanReadUseCase.FindDetailPlanResult updateDetailPlan(DetailPlanUpdateCommand command);

	void deleteDetailPlan(DetailPlanDeleteCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanCreateCommand {
		private final Long planId;
		private final String content;
		private final Boolean admin;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanUpdateCommand {
		private final Long detailPlanId;
		private final Long planId;
		private final String content;
		private final Boolean admin;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanDeleteCommand {
		private final Long detailPlanId;
		private final Long planId;
		private final Boolean admin;
	}
}
