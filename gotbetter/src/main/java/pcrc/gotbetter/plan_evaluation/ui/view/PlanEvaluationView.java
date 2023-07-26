package pcrc.gotbetter.plan_evaluation.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan_evaluation.service.PlanEvaluationReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanEvaluationView {
	private final Long plan_id;
	private final Boolean rejected;
	private final Integer dislike_count;
	private final Boolean checked;

	@Builder
	public PlanEvaluationView(PlanEvaluationReadUseCase.FindPlanEvaluationResult planEvaluationResult) {
		this.plan_id = planEvaluationResult.getPlanId();
		this.rejected = planEvaluationResult.getRejected();
		this.dislike_count = planEvaluationResult.getDislikeCount();
		this.checked = planEvaluationResult.getChecked();
	}
}
