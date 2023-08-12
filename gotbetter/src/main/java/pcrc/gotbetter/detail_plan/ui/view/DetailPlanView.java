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
	private final Boolean rejected;
	private final Long plan_id;
	private final Integer detail_plan_dislike_count;
	private final Boolean detail_plan_dislike_checked;
	// for admin
	private final String created_date;
	private final String updated_date;

	@Builder
	public DetailPlanView(DetailPlanReadUseCase.FindDetailPlanResult detailPlanResult) {
		this.detail_plan_id = detailPlanResult.getDetailPlanId();
		this.content = detailPlanResult.getContent();
		this.complete = detailPlanResult.getComplete();
		this.rejected = detailPlanResult.getRejected();
		this.plan_id = detailPlanResult.getPlanId();
		this.detail_plan_dislike_count = detailPlanResult.getDetailPlanDislikeCount();
		this.detail_plan_dislike_checked = detailPlanResult.getDetailPlanDislikeChecked();
		this.created_date = detailPlanResult.getCreatedDate();
		this.updated_date = detailPlanResult.getUpdatedDate();
	}
}
