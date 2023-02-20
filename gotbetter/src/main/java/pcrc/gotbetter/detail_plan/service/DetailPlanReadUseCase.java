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
        private final String approve_comment;
        private final Boolean rejected;
        private final Long plan_id;
        private final Integer dislike_count;
        private final Boolean checked;

        public static FindDetailPlanResult findByDetailPlan(DetailPlan detailPlan,
                                                            Integer dislike_count, Boolean checked) {
            String approve_comment = detailPlan.getApprove_comment();
            if (approve_comment == null) {
                approve_comment = "";
            }
            return FindDetailPlanResult.builder()
                    .detail_plan_id(detailPlan.getDetailPlanId())
                    .content(detailPlan.getContent())
                    .complete(detailPlan.getComplete())
                    .approve_comment(approve_comment)
                    .rejected(detailPlan.getRejected())
                    .plan_id(detailPlan.getPlanId())
                    .dislike_count(dislike_count)
                    .checked(checked)
                    .build();
        }
    }
}
