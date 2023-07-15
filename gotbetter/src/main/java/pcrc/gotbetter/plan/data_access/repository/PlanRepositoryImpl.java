package pcrc.gotbetter.plan.data_access.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.plan.data_access.entity.QPlan.plan;
import static pcrc.gotbetter.room.data_access.entity.QRoom.room;

public class PlanRepositoryImpl implements PlanQueryRepository {
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

    @Override
    public List<HashMap<String, Object>> findPushNotification() {
        List<Tuple> tuples = queryFactory
            .select(room.roomId, room.title, room.startDate, room.week, room.currentWeek,
                plan.participantInfo.userId, plan.startDate, plan.threeDaysPassed)
            .from(room)
            .leftJoin(plan).on(room.roomId.eq(plan.participantInfo.roomId))
            .where(room.currentWeek.eq(plan.week))
            .fetch();
        List<HashMap<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (Tuple tuple: tuples) {
            Integer week = tuple.get(room.week);
            Integer currentWeek = tuple.get(room.currentWeek);
            LocalDate startDate = tuple.get(room.startDate);
            if (Objects.equals(week, currentWeek)) {
                assert startDate != null;
                assert currentWeek != null;
                LocalDate lastDate = startDate.plusDays(7L * currentWeek - 1);
                if (now.isAfter(lastDate)) {
                    continue;
                }
            }
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("roomId", tuple.get(room.roomId));
            hashMap.put("title", tuple.get(room.title));
            hashMap.put("startDate", tuple.get(room.startDate));
            hashMap.put("week", tuple.get(room.week));
            hashMap.put("currentWeek", tuple.get(room.currentWeek));
            hashMap.put("userId", tuple.get(plan.participantInfo.userId));
            hashMap.put("planStartDate", tuple.get(plan.startDate));
            hashMap.put("threeDaysPassed", tuple.get(plan.threeDaysPassed));
            result.add(hashMap);
        }
        return result;
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
