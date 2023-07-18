package pcrc.gotbetter.detail_plan.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.List;

public interface DetailPlanReadUseCase {

    List<FindDetailPlanResult> getDetailPlans(Long planId);

    @Getter
    @ToString
    @Builder
    class FindDetailPlanResult {
        private final Long detailPlanId;
        private final String content;
        private final Boolean complete;
        private final String approveComment;
        private final Boolean rejected;
        private final Long planId;
        private final Integer detailPlanDislikeCount;
        private final Boolean detailPlanDislikeChecked;

        public static FindDetailPlanResult findByDetailPlan(DetailPlan detailPlan,
                                                            Integer dislikeCount, Boolean checked) {
            String comment = detailPlan.getApproveComment();
            if (comment == null) {
                comment = "";
            }
            return FindDetailPlanResult.builder()
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
