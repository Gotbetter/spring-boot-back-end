package pcrc.gotbetter.plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import static pcrc.gotbetter.plan.data_access.entity.QPlan.plan;

public class PlanRepositoryImpl implements PlanRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public PlanRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateRejected(Long plan_id, Boolean change) {
        queryFactory
                .update(plan)
                .set(plan.rejected, change)
                .where(eqPlanId(plan_id))
                .execute();
    }

    @Override
    @Transactional
    public void updateThreeDaysPassed(Long plan_id) {
        queryFactory
                .update(plan)
                .set(plan.threeDaysPassed, true)
                .where(eqPlanId(plan_id))
                .execute();
    }

    @Override
    public Plan findWeekPlanOfUser(Long participant_id, Integer week) {
        return queryFactory
                .select(plan)
                .from(plan)
                .where(eqParticipantId(participant_id), eqWeek(week))
                .fetchFirst();
    }

    @Override
    public Boolean existsByParticipantId(Long participant_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(plan)
                .where(eqParticipantId(participant_id))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public Boolean existsByThreeDaysPassed(Long plan_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(plan)
                .where(eqPlanId(plan_id), eqThreeDaysPassed(true))
                .fetchFirst();
        return exists != null;
    }

    /**
     * plan eq
     */
    private BooleanExpression eqPlanId(Long plan_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(plan_id))) {
            return null;
        }
        return plan.planId.eq(plan_id);
    }

    private BooleanExpression eqParticipantId(Long participant_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(participant_id))) {
            return null;
        }
        return plan.participantInfo.participantId.eq(participant_id);
    }

    private BooleanExpression eqWeek(Integer week) {
        if (StringUtils.isNullOrEmpty(String.valueOf(week))) {
            return null;
        }
        return plan.week.eq(week);
    }

    private BooleanExpression eqThreeDaysPassed(Boolean threeDaysPassed) {
        if (StringUtils.isNullOrEmpty(String.valueOf(threeDaysPassed))) {
            return null;
        }
        return plan.threeDaysPassed.eq(threeDaysPassed);
    }
}
