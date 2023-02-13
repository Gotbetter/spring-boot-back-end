package pcrc.gotbetter.plan.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.util.Optional;

import static pcrc.gotbetter.plan.data_access.entity.QPlan.plan;

public class PlanRepositoryQueryDSLImpl implements PlanRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public PlanRepositoryQueryDSLImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
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
    public Optional<Plan> findWeekPlanOfUser(Long participant_id, Integer week) {
        return Optional.ofNullable(queryFactory
                .select(plan)
                .from(plan)
                .where(eqParticipantId(participant_id), eqWeek(week))
                .fetchFirst());
    }

    /**
     * plan eq
     */
    private BooleanExpression eqParticipantId(Long participant_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(participant_id))) {
            return null;
        }
        return plan.participantId.eq(participant_id);
    }

    private BooleanExpression eqWeek(Integer week) {
        if (StringUtils.isNullOrEmpty(String.valueOf(week))) {
            return null;
        }
        return plan.week.eq(week);
    }
}
