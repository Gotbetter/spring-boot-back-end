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
    private final Boolean rejected;
    private final Long plan_id;
    private final Integer detail_plan_dislike_count;
    private final Boolean detail_plan_dislike_checked;

    @Builder
    public DetailPlanEvaluationView(DetailPlanEvalReadUseCase.FindDetailPlanEvalResult detailPlanEvalResult) {
        this.detail_plan_id = detailPlanEvalResult.getDetailPlanId();
        this.content = detailPlanEvalResult.getContent();
        this.complete = detailPlanEvalResult.getComplete();
        this.rejected = detailPlanEvalResult.getRejected();
        this.plan_id = detailPlanEvalResult.getPlanId();
        this.detail_plan_dislike_count = detailPlanEvalResult.getDetailPlanDislikeCount();
        this.detail_plan_dislike_checked = detailPlanEvalResult.getDetailPlanDislikeChecked();
    }
}
