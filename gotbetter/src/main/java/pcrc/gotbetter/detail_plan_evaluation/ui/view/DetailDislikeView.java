package pcrc.gotbetter.detail_plan_evaluation.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailDislikeView {
	private final Long detail_plan_id;
	private final Long plan_id;
	private final Long user_id;
	private final String username;
	private final String profile;
	private final String created_date;
	// for admin
	private final Long participant_id;

	@Builder
	public DetailDislikeView(DetailPlanEvalReadUseCase.FindDetailDislikeListResult detailDislikeListResult) {
		this.detail_plan_id = detailDislikeListResult.getDetailPlanId();
		this.plan_id = detailDislikeListResult.getPlanId();
		this.user_id = detailDislikeListResult.getUserId();
		this.username = detailDislikeListResult.getUsername();
		this.profile = detailDislikeListResult.getProfile();
		this.created_date = detailDislikeListResult.getCreatedDate();
		this.participant_id = detailDislikeListResult.getParticipantId();
	}
}
