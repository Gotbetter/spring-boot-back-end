package pcrc.gotbetter.plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.util.List;

import static pcrc.gotbetter.plan.data_access.entity.QPlan.plan;

public class PlanRepositoryImpl implements PlanRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public PlanRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateRejected(Long planId, Boolean change) {
        queryFactory
                .update(plan)
                .set(plan.rejected, change)
                .where(eqPlanId(planId))
                .execute();
    }

    @Override
    @Transactional
    public void updateThreeDaysPassed(Long planId) {
        queryFactory
                .update(plan)
                .set(plan.threeDaysPassed, true)
                .where(eqPlanId(planId))
                .execute();
    }

    @Override
    public Plan findWeekPlanOfUser(Long participantId, Integer week) {
        return queryFactory
                .select(plan)
                .from(plan)
                .where(eqParticipantId(participantId), eqWeek(week))
                .fetchFirst();
    }

    @Override
    public Boolean existsByParticipantId(Long participantId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(plan)
                .where(eqParticipantId(participantId))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public List<Plan> findListByRoomId(Long roomId, Integer passedWeek) {
        return queryFactory
                .selectFrom(plan)
                .where(eqRoomId(roomId), eqWeek(passedWeek))
                .fetch();
    }

    /**
     * plan eq
     */
    private BooleanExpression eqPlanId(Long planId) {
        return planId == null ? null : plan.planId.eq(planId);
    }

    private BooleanExpression eqParticipantId(Long participantId) {
        return participantId == null ? null : plan.participantInfo.participantId.eq(participantId);
    }

    private BooleanExpression eqRoomId(Long roomId) {
        return roomId == null ? null : plan.participantInfo.roomId.eq(roomId);
    }

    private BooleanExpression eqWeek(Integer week) {
        return week == null ? null : plan.week.eq(week);
    }
}
