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
    private final Long plan_id;

    @Builder
    public DetailPlanView(DetailPlanReadUseCase.FindDetailPlanResult detailPlanResult) {
        this.detail_plan_id = detailPlanResult.getDetail_plan_id();
        this.content = detailPlanResult.getContent();
        this.complete = detailPlanResult.getComplete();
        this.plan_id = detailPlanResult.getPlan_id();
    }
}
