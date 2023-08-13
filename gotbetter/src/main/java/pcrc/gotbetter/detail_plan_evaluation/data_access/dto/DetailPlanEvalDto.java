package pcrc.gotbetter.detail_plan_evaluation.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.user.data_access.entity.User;

@Getter
@AllArgsConstructor
public class DetailPlanEvalDto {
	private DetailPlanEval detailPlanEval;
	private User user;
}
