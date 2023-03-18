package pcrc.gotbetter.detail_plan.data_access.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.detailPlan;

public class DetailPlanRepositoryImpl implements DetailPlanRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public DetailPlanRepositoryImpl(JPAQueryFactory queryFactory, EntityManager em) {
        this.queryFactory = queryFactory;
        this.em = em;
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

    @Override
    public HashMap<String, Long> countCompleteTrue(Long plan_id) {
        Query query = em.createQuery(
                "SELECT count(p) as size, count(if(p.complete=true, p.complete, null))" +
                " FROM DetailPlan p WHERE p.planId = " + plan_id);
        Object[] object = (Object[]) query.getResultList().get(0);
        HashMap<String, Long> result = new HashMap<>();
        result.put("size", (Long) object[0]);
        result.put("completeCount", (Long) object[1]);
        em.clear();
        return result;
    }

    /**
     * participant eq
     */
    private BooleanExpression detailPlanEqDetailPlanId(Long detail_plan_id) {
        return detail_plan_id == null ? null : detailPlan.detailPlanId.eq(detail_plan_id);
    }

    private BooleanExpression detailPlanEqPlanId(Long plan_id) {
        return plan_id == null ? null : detailPlan.planId.eq(plan_id);
    }
}
