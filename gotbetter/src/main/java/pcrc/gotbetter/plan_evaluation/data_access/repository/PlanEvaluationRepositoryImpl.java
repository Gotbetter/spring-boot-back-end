package pcrc.gotbetter.plan_evaluation.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;

import static pcrc.gotbetter.plan_evaluation.data_access.entity.QPlanEvaluation.planEvaluation;

public class PlanEvaluationRepositoryImpl implements PlanEvaluationRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    public PlanEvaluationRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void deletePlanEvaluation(Long plan_id, Long participant_id) {
        queryFactory
                .delete(planEvaluation)
                .where(planEvaluationEqPlanId(plan_id),
                        planEvaluationEqParticipantId(participant_id))
                .execute();
    }

    @Override
    public Boolean existsEval(Long plan_id, Long participant_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(planEvaluation)
                .where(planEvaluationEqPlanId(plan_id),
                        planEvaluationEqParticipantId(participant_id))
                .fetchFirst();
        return exists != null;
    }

    /**
     * planEvaluation eq
     */
    private BooleanExpression planEvaluationEqPlanId(Long plan_id) {
        return plan_id == null ? null : planEvaluation.planEvaluationId.planId.eq(plan_id);
    }

    private BooleanExpression planEvaluationEqParticipantId(Long participant_id) {
        return participant_id == null ? null : planEvaluation.planEvaluationId.participantId.eq(participant_id);
    }
}
