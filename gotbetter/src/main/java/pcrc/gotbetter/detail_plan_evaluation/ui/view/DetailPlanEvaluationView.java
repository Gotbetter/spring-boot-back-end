package pcrc.gotbetter.detail_plan_evaluation.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailPlanEvaluationView {
    private final Long detail_plan_id;
    private final String content;
    private final Boolean complete;
    private final String approve_comment;
    private final Boolean rejected;
    private final Long plan_id;

    @Builder
    public DetailPlanEvaluationView(DetailPlanEvalReadUseCase.FindDetailPlanEvalResult detailPlanEvalResult) {
        this.detail_plan_id = detailPlanEvalResult.getDetail_plan_id();
        this.content = detailPlanEvalResult.getContent();
        this.complete = detailPlanEvalResult.getComplete();
        this.approve_comment = detailPlanEvalResult.getApprove_comment();
        this.rejected = detailPlanEvalResult.getRejected();
        this.plan_id = detailPlanEvalResult.getPlan_id();
    }
}
