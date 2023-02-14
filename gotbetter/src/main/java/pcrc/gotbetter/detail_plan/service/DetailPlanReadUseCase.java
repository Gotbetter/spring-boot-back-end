package pcrc.gotbetter.detail_plan.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.List;

public interface DetailPlanReadUseCase {

    List<FindDetailPlanResult> getDetailPlans(Long plan_id);

    @Getter
    @ToString
    @Builder
    class FindDetailPlanResult {
        private final Long detail_plan_id;
        private final String content;
        private final Boolean complete;
        private final Long plan_id;

        public static FindDetailPlanResult findByDetailPlan(DetailPlan detailPlan) {
            return FindDetailPlanResult.builder()
                    .detail_plan_id(detailPlan.getDetailPlanId())
                    .content(detailPlan.getContent())
                    .complete(detailPlan.getComplete())
                    .plan_id(detailPlan.getPlanId())
                    .build();
        }
    }
}
