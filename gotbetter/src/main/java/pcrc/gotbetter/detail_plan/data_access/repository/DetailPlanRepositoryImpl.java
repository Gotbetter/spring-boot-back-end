package pcrc.gotbetter.detail_plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;

import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.detailPlan;

public class DetailPlanRepositoryImpl implements DetailPlanRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    public DetailPlanRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateDetailContent(Long detail_plan_id, String content) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.content, content)
                .where(detailPlanEqDetailPlanId(detail_plan_id))
                .execute();
    }

    @Override
    @Transactional
    public void updateRejected(Long detail_plan_id, Boolean rejected) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.rejected, rejected)
                .where(detailPlanEqDetailPlanId(detail_plan_id))
                .execute();
    }

    @Override
    @Transactional
    public void updateDetailPlanCompleted(Long detail_plan_id, String approve_comment) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.complete, true)
                .set(detailPlan.approve_comment, approve_comment)
                .where(detailPlanEqDetailPlanId(detail_plan_id))
                .execute();
    }

    @Override
    @Transactional
    public void updateDetailPlanUndo(Long detail_plan_id, Boolean rejected) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.complete, false)
                .setNull(detailPlan.approve_comment)
                .set(detailPlan.rejected, rejected)
                .where(detailPlanEqDetailPlanId(detail_plan_id))
                .execute();
    }

    @Override
    @Transactional
    public void deleteDetailPlan(Long detail_plan_id) {
        queryFactory
                .delete(detailPlan)
                .where(detailPlanEqDetailPlanId(detail_plan_id))
                .execute();
    }

    @Override
    public Boolean existsByPlanId(Long plan_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(detailPlan)
                .where(detailPlanEqPlanId(plan_id))
                .fetchFirst();
        return exists != null;
    }

    /**
     * participant eq
     */
    private BooleanExpression detailPlanEqDetailPlanId(Long detail_plan_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(detail_plan_id))) {
            return null;
        }
        return detailPlan.detailPlanId.eq(detail_plan_id);
    }

    private BooleanExpression detailPlanEqPlanId(Long plan_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(plan_id))) {
            return null;
        }
        return detailPlan.planId.eq(plan_id);
    }
}
