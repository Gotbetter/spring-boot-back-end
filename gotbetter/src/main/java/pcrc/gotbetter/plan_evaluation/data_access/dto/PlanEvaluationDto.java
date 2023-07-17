package pcrc.gotbetter.plan_evaluation.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;

@Getter
@AllArgsConstructor
public class PlanEvaluationDto {
	private PlanEvaluation planEvaluation;
	private Plan plan;
}
