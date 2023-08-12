package pcrc.gotbetter.plan_evaluation.data_access.repository;

import static pcrc.gotbetter.plan_evaluation.data_access.entity.QPlanEvaluation.*;
import static pcrc.gotbetter.user.data_access.entity.QUser.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.plan_evaluation.data_access.dto.PlanEvaluationDto;

public class PlanEvaluationRepositoryImpl implements PlanEvaluationQueryRepository {
	private final JPAQueryFactory queryFactory;

	public PlanEvaluationRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Boolean existsEval(Long planId, Long participantId) {
		Integer exists = queryFactory
			.selectOne()
			.from(planEvaluation)
			.where(planEvaluationEqPlanId(planId),
				planEvaluationEqParticipantId(participantId))
			.fetchFirst();
		return exists != null;
	}

	@Override
	public List<PlanEvaluationDto> findDislikeUsers(Long planId) {
		return queryFactory
			.select(Projections.constructor(PlanEvaluationDto.class,
				planEvaluation, user))
			.from(planEvaluation)
			.leftJoin(user).on(planEvaluation.planEvaluationId.userId.eq(user.userId)).fetchJoin()
			.where(planEvaluationEqPlanId(planId))
			.fetch();
	}

	/**
	 * planEvaluation eq
	 */
	private BooleanExpression planEvaluationEqPlanId(Long planId) {
		return planId == null ? null : planEvaluation.planEvaluationId.planId.eq(planId);
	}

	private BooleanExpression planEvaluationEqParticipantId(Long participantId) {
		return participantId == null ? null : planEvaluation.planEvaluationId.participantId.eq(participantId);
	}
}
