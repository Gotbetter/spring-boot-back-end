package pcrc.gotbetter.plan.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan.service.PlanReadUseCase;

import java.time.LocalDate;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanView {
    private final Long plan_id;
    private final LocalDate start_date;
    private final LocalDate target_date;
    private final Float score;
    private final Integer week;
    private final Boolean three_days_passed;
    private final Boolean rejected;
    private final Long id;
    private final Long room_id;

    @Builder
    public PlanView(PlanReadUseCase.FindPlanResult planResult) {
        this.plan_id = planResult.getPlan_id();
        this.start_date = planResult.getStart_date();
        this.target_date = planResult.getTarget_date();
        this.score = planResult.getScore();
        this.week = planResult.getWeek();
        this.three_days_passed = planResult.getThree_days_passed();
        this.rejected = planResult.getRejected();
        this.id = planResult.getUser_id();
        this.room_id = planResult.getRoom_id();
    }
}
