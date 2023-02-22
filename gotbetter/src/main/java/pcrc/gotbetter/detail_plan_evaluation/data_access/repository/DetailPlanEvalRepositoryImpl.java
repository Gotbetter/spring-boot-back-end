package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static pcrc.gotbetter.detail_plan_evaluation.data_access.entity.QDetailPlanEval.detailPlanEval;

public class DetailPlanEvalRepositoryImpl implements DetailPlanEvalRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    public DetailPlanEvalRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsEval(Long detail_plan_id, Long participant_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(detailPlanEval)
                .where(detailPlanEvalEqDetailPlanId(detail_plan_id),
                        detailPlanEvalEqParticipantId(participant_id))
                .fetchFirst();
        return exists != null;
    }

    /**
     * detailPlanEval eq
     */
    private BooleanExpression detailPlanEvalEqDetailPlanId(Long detail_plan_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(detail_plan_id))) {
            return null;
        }
        return detailPlanEval.detailPlanEvalId.detailPlanId.eq(detail_plan_id);
    }

    private BooleanExpression detailPlanEvalEqParticipantId(Long participant_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(participant_id))) {
            return null;
        }
        return detailPlanEval.detailPlanEvalId.participantId.eq(participant_id);
    }
}
