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

    @Override
    public List<Plan> findListByRoomId(Long room_id, Integer passedWeek) {
        return queryFactory
                .selectFrom(plan)
                .where(eqRoomId(room_id), eqWeek(passedWeek))
                .fetch();
    }

    /**
     * plan eq
     */
    private BooleanExpression eqPlanId(Long plan_id) {
        return plan_id == null ? null : plan.planId.eq(plan_id);
    }

    private BooleanExpression eqParticipantId(Long participant_id) {
        return participant_id == null ? null : plan.participantInfo.participantId.eq(participant_id);
    }

    private BooleanExpression eqRoomId(Long room_id) {
        return room_id == null ? null : plan.participantInfo.roomId.eq(room_id);
    }

    private BooleanExpression eqWeek(Integer week) {
        return week == null ? null : plan.week.eq(week);
    }

    private BooleanExpression eqThreeDaysPassed(Boolean threeDaysPassed) {
        return threeDaysPassed == null ? null : plan.threeDaysPassed.eq(threeDaysPassed);
    }
}
