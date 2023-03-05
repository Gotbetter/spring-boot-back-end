package pcrc.gotbetter.detail_plan.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan.service.DetailPlanReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailPlanView {
    private final Long detail_plan_id;
    private final String content;
    private final Boolean complete;
    private final String approve_comment;
    private final Boolean rejected;
    private final Long plan_id;
    private final Integer detail_plan_dislike_count;
    private final Boolean detail_plan_dislike_checked;

    @Builder
    public DetailPlanView(DetailPlanReadUseCase.FindDetailPlanResult detailPlanResult) {
        this.detail_plan_id = detailPlanResult.getDetail_plan_id();
        this.content = detailPlanResult.getContent();
        this.complete = detailPlanResult.getComplete();
        this.approve_comment = detailPlanResult.getApprove_comment();
        this.rejected = detailPlanResult.getRejected();
        this.plan_id = detailPlanResult.getPlan_id();
        this.detail_plan_dislike_count = detailPlanResult.getDetail_plan_dislike_count();
        this.detail_plan_dislike_checked = detailPlanResult.getDetail_plan_dislike_checked();
    }
}
