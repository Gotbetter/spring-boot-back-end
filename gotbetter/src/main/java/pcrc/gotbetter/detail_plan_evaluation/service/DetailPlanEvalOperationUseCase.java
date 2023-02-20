package pcrc.gotbetter.detail_plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanEvalOperationUseCase {
    void createDetailPlanEvaluation(DetailPlanEvaluationCommand command);
    void deleteDetailPlanEvaluation(DetailPlanEvaluationCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class DetailPlanEvaluationCommand {
        private final Long detail_plan_id;
    }
}
