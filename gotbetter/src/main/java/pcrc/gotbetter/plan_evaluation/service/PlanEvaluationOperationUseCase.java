package pcrc.gotbetter.plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface PlanEvaluationOperationUseCase {
    PlanEvaluationReadUseCase.FindPlanEvaluationResult createPlanEvaluation(PlanEvaluationCommand command);
    void deletePlanEvaluation(PlanEvaluationCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class PlanEvaluationCommand {
        private final Long planId;
    }
}
