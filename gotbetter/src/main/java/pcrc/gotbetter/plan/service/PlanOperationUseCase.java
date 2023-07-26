package pcrc.gotbetter.plan.service;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface PlanOperationUseCase {

	List<PlanReadUseCase.FindPlanResult> createPlans(PlanCreateCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class PlanCreateCommand {
		private final Long participantId;
	}
}
