package pcrc.gotbetter.plan.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.time.LocalDate;

public interface PlanReadUseCase {

    FindPlanResult getWeekPlan(PlanFindQuery query);

    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    @Builder
    class PlanFindQuery {
        private final Long room_id;
        private final Long user_id;
        private final Integer week;
    }

    @Getter
    @ToString
    @Builder
    class FindPlanResult {
        private final Long plan_id;
        private final LocalDate start_date;
        private final LocalDate target_date;
        private final Float score;
        private final Integer week;
        private final Boolean three_days_passed;
        private final Boolean rejected;
        private final Long user_id;
        private final Long room_id;

        public static FindPlanResult findByPlan(Plan plan) {
            return FindPlanResult.builder()
                    .plan_id(plan.getPlanId())
                    .start_date(plan.getStartDate())
                    .target_date(plan.getTargetDate())
                    .score(plan.getScore())
                    .week(plan.getWeek())
                    .three_days_passed(plan.getThreeDaysPassed())
                    .rejected(plan.getRejected())
                    .user_id(plan.getUserId())
                    .room_id(plan.getRoomId())
                    .build();
        }
    }
}
