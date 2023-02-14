package pcrc.gotbetter.detail_plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;

import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.detailPlan;

public class DetailPlanRepositoryQueryDSLImpl implements DetailPlanRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    public DetailPlanRepositoryQueryDSLImpl(JPAQueryFactory queryFactory) {
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
    public void deleteDetailPlan(Long detail_plan_id) {
        queryFactory
                .delete(detailPlan)
                .where(detailPlanEqDetailPlanId(detail_plan_id))
                .execute();
    }

    /**
     * participant eq
     */
    private BooleanExpression detailPlanEqUserId(Long user_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(user_id))) {
            return null;
        }
        return detailPlan.userId.eq(user_id);
    }
    private BooleanExpression detailPlanEqRoomId(Long room_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_id))) {
            return null;
        }
        return detailPlan.roomId.eq(room_id);
    }
    private BooleanExpression detailPlanEqDetailPlanId(Long detail_plan_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(detail_plan_id))) {
            return null;
        }
        return detailPlan.detailPlanId.eq(detail_plan_id);
    }
}
