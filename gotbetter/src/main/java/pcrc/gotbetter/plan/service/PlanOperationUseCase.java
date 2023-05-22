package pcrc.gotbetter.plan.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

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
