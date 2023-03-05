package pcrc.gotbetter.detail_plan_evaluation.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

public interface DetailPlanEvalReadUseCase {
    @Getter
    @ToString
    @Builder
    class FindDetailPlanEvalResult {
        private final Long detail_plan_id;
        private final String content;
        private final Boolean complete;
        private final String approve_comment;
        private final Boolean rejected;
        private final Long plan_id;

        public static FindDetailPlanEvalResult findByDetailPlanEval(DetailPlan detailPlan, Boolean rejected) {
            Boolean is_rejected = rejected == null ? detailPlan.getRejected() : rejected;
            return FindDetailPlanEvalResult.builder()
                    .detail_plan_id(detailPlan.getDetailPlanId())
                    .content(detailPlan.getContent())
                    .complete(detailPlan.getComplete())
                    .approve_comment(detailPlan.getApprove_comment())
                    .rejected(is_rejected)
                    .plan_id(detailPlan.getPlanId())
                    .build();
        }
    }
}
