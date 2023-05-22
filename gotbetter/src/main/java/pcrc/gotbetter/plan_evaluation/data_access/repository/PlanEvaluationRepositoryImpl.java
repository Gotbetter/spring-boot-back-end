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
    public void deletePlanEvaluation(Long planId, Long participantId) {
        queryFactory
                .delete(planEvaluation)
                .where(planEvaluationEqPlanId(planId),
                        planEvaluationEqParticipantId(participantId))
                .execute();
    }

    @Override
    public Boolean existsEval(Long planId, Long participantId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(planEvaluation)
                .where(planEvaluationEqPlanId(planId),
                        planEvaluationEqParticipantId(participantId))
                .fetchFirst();
        return exists != null;
    }

    /**
     * planEvaluation eq
     */
    private BooleanExpression planEvaluationEqPlanId(Long planId) {
        return planId == null ? null : planEvaluation.planEvaluationId.planId.eq(planId);
    }

    private BooleanExpression planEvaluationEqParticipantId(Long participantId) {
        return participantId == null ? null : planEvaluation.planEvaluationId.participantId.eq(participantId);
    }
}
