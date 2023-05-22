package pcrc.gotbetter.plan.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.plan.data_access.entity.Plan;

public interface PlanReadUseCase {

    FindPlanResult getWeekPlan(PlanFindQuery query);

    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    @Builder
    class PlanFindQuery {
        private final Long participantId;
        private final Integer week;
    }

    @Getter
    @ToString
    @Builder
    class FindPlanResult {
        private final Long planId;
        private final String startDate;
        private final String targetDate;
        private final Float score;
        private final Integer week;
        private final Boolean threeDaysPassed;
        private final Boolean rejected;
        private final Long userId;
        private final Long roomId;

        public static FindPlanResult findByPlan(Plan plan) {
            return FindPlanResult.builder()
                    .planId(plan.getPlanId())
                    .startDate(plan.getStartDate().toString())
                    .targetDate(plan.getTargetDate().toString())
                    .score(plan.getScore())
                    .week(plan.getWeek())
                    .threeDaysPassed(plan.getThreeDaysPassed())
                    .rejected(plan.getRejected())
                    .userId(plan.getParticipantInfo().getUserId())
                    .roomId(plan.getParticipantInfo().getRoomId())
                    .build();
        }
    }
}
