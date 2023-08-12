package pcrc.gotbetter.plan_evaluation.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;
import pcrc.gotbetter.user.data_access.entity.User;

@Getter
@AllArgsConstructor
public class PlanEvaluationDto {
	private PlanEvaluation planEvaluation;
	private Plan plan;
	private User user;

	public PlanEvaluationDto(PlanEvaluation planEvaluation, User user) {
		this.planEvaluation = planEvaluation;
		this.user = user;
	}
}
