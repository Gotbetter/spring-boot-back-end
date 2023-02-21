package pcrc.gotbetter.detail_plan.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanCompleteOperationUseCase {

    DetailPlanReadUseCase.FindDetailPlanResult completeDetailPlan(DetailPlanCompleteCommand command);
    DetailPlanReadUseCase.FindDetailPlanResult undoCompleteDetailPlan(DetailPlanCompleteCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class DetailPlanCompleteCommand {
        private final Long plan_id;
        private final Long detail_plan_id;
        private final String approve_comment;
    }
}
