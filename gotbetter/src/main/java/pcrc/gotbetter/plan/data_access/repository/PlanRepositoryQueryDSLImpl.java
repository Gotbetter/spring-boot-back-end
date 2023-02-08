package pcrc.gotbetter.plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.service.PlanReadUseCase;

import java.util.Optional;

import static pcrc.gotbetter.plan.data_access.entity.QPlan.plan;

public class PlanRepositoryQueryDSLImpl implements PlanRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public PlanRepositoryQueryDSLImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsByRoomidAndUserid(Long room_id, Long user_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(plan)
                .where(eqRoomId(room_id), eqUserId(user_id))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public Optional<Plan> findWeekPlanOfUser(PlanReadUseCase.PlanFindQuery query) {
        return Optional.ofNullable(queryFactory
                .select(plan)
                .from(plan)
                .where(eqRoomId(query.getRoom_id()), eqUserId(query.getUser_id())
                        , eqWeek(query.getWeek()))
                .fetchFirst());
    }

    /**
     * plan eq
     */
    private BooleanExpression eqUserId(Long user_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(user_id))) {
            return null;
        }
        return plan.userId.eq(user_id);
    }

    private BooleanExpression eqRoomId(Long room_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_id))) {
            return null;
        }
        return plan.roomId.eq(room_id);
    }

    private BooleanExpression eqWeek(Integer week) {
        if (StringUtils.isNullOrEmpty(String.valueOf(week))) {
            return null;
        }
        return plan.week.eq(week);
    }
}
