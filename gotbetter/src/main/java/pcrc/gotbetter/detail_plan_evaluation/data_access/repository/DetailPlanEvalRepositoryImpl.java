package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static pcrc.gotbetter.detail_plan_evaluation.data_access.entity.QDetailPlanEval.detailPlanEval;

public class DetailPlanEvalRepositoryImpl implements DetailPlanEvalQueryRepository {
    private final JPAQueryFactory queryFactory;

    public DetailPlanEvalRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsEval(Long detailPlanId, Long participantId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(detailPlanEval)
                .where(detailPlanEvalEqDetailPlanId(detailPlanId),
                        detailPlanEvalEqParticipantId(participantId))
                .fetchFirst();
        return exists != null;
    }

    /**
     * detailPlanEval eq
     */
    private BooleanExpression detailPlanEvalEqDetailPlanId(Long detailPlanId) {
        return detailPlanId == null ? null : detailPlanEval.detailPlanEvalId.detailPlanId.eq(detailPlanId);
    }

    private BooleanExpression detailPlanEvalEqParticipantId(Long participantId) {
        return participantId == null ? null : detailPlanEval.detailPlanEvalId.participantId.eq(participantId);
    }
}
