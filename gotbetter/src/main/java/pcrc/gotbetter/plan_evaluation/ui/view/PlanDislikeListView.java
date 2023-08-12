package pcrc.gotbetter.plan_evaluation.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan_evaluation.service.PlanEvaluationReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanDislikeListView {
	private final Long plan_id;
	private final Long user_id;
	private final String username;
	private final String profile;
	private final String created_date;

	@Builder
	public PlanDislikeListView(PlanEvaluationReadUseCase.FindPlanDislikeListResult planDislikeListResult) {
		this.plan_id = planDislikeListResult.getPlanId();
		this.user_id = planDislikeListResult.getUserId();
		this.username = planDislikeListResult.getUsername();
		this.profile = planDislikeListResult.getProfile();
		this.created_date = planDislikeListResult.getCreatedDate();
	}
}
