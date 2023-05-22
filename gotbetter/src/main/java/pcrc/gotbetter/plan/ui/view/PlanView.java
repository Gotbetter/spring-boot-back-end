package pcrc.gotbetter.plan.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan.service.PlanReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanView {
    private final Long plan_id;
    private final String start_date;
    private final String target_date;
    private final Float score;
    private final Integer week;
    private final Boolean three_days_passed;
    private final Boolean rejected;
    private final Long user_id;
    private final Long room_id;

    @Builder
    public PlanView(PlanReadUseCase.FindPlanResult planResult) {
        this.plan_id = planResult.getPlanId();
        this.start_date = planResult.getStartDate();
        this.target_date = planResult.getTargetDate();
        this.score = planResult.getScore();
        this.week = planResult.getWeek();
        this.three_days_passed = planResult.getThreeDaysPassed();
        this.rejected = planResult.getRejected();
        this.user_id = planResult.getUserId();
        this.room_id = planResult.getRoomId();
    }
}
