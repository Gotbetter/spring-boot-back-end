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
        private final Long plan_id;
    }

    @Getter
    @ToString
    @Builder
    class FindPlanEvaluationResult {
        private final Integer dislike_count;
        private final Boolean checked;

        public static FindPlanEvaluationResult findByPlanEvaluation(Integer dislike_count,
                                                                    Boolean checked) {
            return FindPlanEvaluationResult.builder()
                    .dislike_count(dislike_count)
                    .checked(checked)
                    .build();
        }
    }
}