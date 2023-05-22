package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import static pcrc.gotbetter.detail_plan_evaluation.data_access.entity.QDetailPlanEval.detailPlanEval;

public class DetailPlanEvalRepositoryImpl implements DetailPlanEvalRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    public DetailPlanEvalRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void deleteDetailPlanEval(Long detailPlanId, Long participantId) {
        queryFactory.delete(detailPlanEval)
                .where(detailPlanEvalEqDetailPlanId(detailPlanId),
                        detailPlanEvalEqParticipantId(participantId))
                .execute();
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
