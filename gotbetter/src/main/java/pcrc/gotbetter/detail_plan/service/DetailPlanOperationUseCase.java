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
        private final Long plan_id;
        private final String content;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class DetailPlanUpdateCommand {
        private final Long detail_plan_id;
        private final Long plan_id;
        private final String content;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class DetailPlanDeleteCommand {
        private final Long detail_plan_id;
        private final Long plan_id;
    }
}
