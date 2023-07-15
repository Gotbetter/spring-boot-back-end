package pcrc.gotbetter.detail_plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.HashMap;

import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.detailPlan;

public class DetailPlanRepositoryImpl implements DetailPlanQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public DetailPlanRepositoryImpl(JPAQueryFactory queryFactory, EntityManager em) {
        this.queryFactory = queryFactory;
        this.em = em;
    }

    @Override
    @Transactional
    public void updateDetailContent(Long detailPlanId, String content) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.content, content)
                .where(detailPlanEqDetailPlanId(detailPlanId))
                .execute();
    }

    @Override
    @Transactional
    public void updateRejected(Long detailPlanId, Boolean rejected) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.rejected, rejected)
                .where(detailPlanEqDetailPlanId(detailPlanId))
                .execute();
    }

    @Override
    @Transactional
    public void updateDetailPlanCompleted(Long detailPlanId, String approveComment) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.complete, true)
                .set(detailPlan.approveComment, approveComment)
                .where(detailPlanEqDetailPlanId(detailPlanId))
                .execute();
    }

    @Override
    @Transactional
    public void updateDetailPlanUndo(Long detailPlanId, Boolean rejected) {
        queryFactory
                .update(detailPlan)
                .set(detailPlan.complete, false)
                .setNull(detailPlan.approveComment)
                .set(detailPlan.rejected, rejected)
                .where(detailPlanEqDetailPlanId(detailPlanId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteDetailPlan(Long detailPlanId) {
        queryFactory
                .delete(detailPlan)
                .where(detailPlanEqDetailPlanId(detailPlanId))
                .execute();
    }

    @Override
    public Boolean existsByPlanId(Long planId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(detailPlan)
                .where(detailPlanEqPlanId(planId))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public HashMap<String, Long> countCompleteTrue(Long planId) {
        Query query = em.createQuery(
                "SELECT count(p) as size, count(if(p.complete=true, p.complete, null))" +
                " FROM DetailPlan p WHERE p.planId = " + planId);
        Object[] object = (Object[]) query.getResultList().get(0);
        HashMap<String, Long> result = new HashMap<>();
        result.put("size", (Long) object[0]);
        result.put("completeCount", (Long) object[1]);
        em.clear();
        return result;
    }

    @Override
    public DetailPlan findByDetailPlanId(Long detailPlanId) {
        return queryFactory
                .selectFrom(detailPlan)
                .where(detailPlanEqDetailPlanId(detailPlanId))
                .fetchFirst();
    }

    /**
     * participant eq
     */
    private BooleanExpression detailPlanEqDetailPlanId(Long detailPlanId) {
        return detailPlanId == null ? null : detailPlan.detailPlanId.eq(detailPlanId);
    }

    private BooleanExpression detailPlanEqPlanId(Long planId) {
        return planId == null ? null : detailPlan.planId.eq(planId);
    }
}
