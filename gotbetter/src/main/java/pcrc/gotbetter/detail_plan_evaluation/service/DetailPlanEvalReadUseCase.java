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
        private final Long detailPlanId;
        private final String content;
        private final Boolean complete;
        private final String approveComment;
        private final Boolean rejected;
        private final Long planId;
        private final Integer detailPlanDislikeCount;
        private final Boolean detailPlanDislikeChecked;

        public static FindDetailPlanEvalResult findByDetailPlanEval(DetailPlan detailPlan,
                                                                    Integer dislikeCount, Boolean checked) {
            String comment = detailPlan.getApproveComment();
            if (comment == null) {
                comment = "";
            }

            return FindDetailPlanEvalResult.builder()
                    .detailPlanId(detailPlan.getDetailPlanId())
                    .content(detailPlan.getContent())
                    .complete(detailPlan.getComplete())
                    .approveComment(comment)
                    .rejected(detailPlan.getRejected())
                    .planId(detailPlan.getPlanId())
                    .detailPlanDislikeCount(dislikeCount)
                    .detailPlanDislikeChecked(checked)
                    .build();
        }
    }
}
