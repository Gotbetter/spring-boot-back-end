package pcrc.gotbetter.plan_evaluation.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface PlanEvaluationReadUseCase {

    FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query);

    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    @Builder
    class PlanEvaluationFindQuery {
        private final Long planId;
    }

    @Getter
    @ToString
    @Builder
    class FindPlanEvaluationResult {
        private final Long planId;
        private final Boolean rejected;
        private final Integer dislikeCount;
        private final Boolean checked;

        public static FindPlanEvaluationResult findByPlanEvaluation(Long planId,
                                                                    Boolean rejected,
                                                                    Integer dislikeCount,
                                                                    Boolean checked) {
            return FindPlanEvaluationResult.builder()
                    .planId(planId)
                    .rejected(rejected)
                    .dislikeCount(dislikeCount)
                    .checked(checked)
                    .build();
        }
    }
}
