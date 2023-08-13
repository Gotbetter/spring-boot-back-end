package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import static pcrc.gotbetter.detail_plan_evaluation.data_access.entity.QDetailPlanEval.*;
import static pcrc.gotbetter.user.data_access.entity.QUser.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.detail_plan_evaluation.data_access.dto.DetailPlanEvalDto;

public class DetailPlanEvalRepositoryImpl implements DetailPlanEvalQueryRepository {
	private final JPAQueryFactory queryFactory;

	public DetailPlanEvalRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Boolean existsEval(Long detailPlanId, Long participantId) {
		Integer exists = queryFactory
			.selectOne()
			.from(detailPlanEval)
			.where(detailPlanEvalEqDetailPlanId(detailPlanId),
				detailPlanEvalEqParticipantId(participantId))
			.fetchFirst();
		return exists != null;
	}

	@Override
	public List<DetailPlanEvalDto> findDislikeUsers(Long detailPlanId) {
		return queryFactory
			.select(Projections.constructor(DetailPlanEvalDto.class,
				detailPlanEval, user))
			.from(detailPlanEval)
			.leftJoin(user).on(detailPlanEval.detailPlanEvalId.userId.eq(user.userId)).fetchJoin()
			.where(detailPlanEvalEqDetailPlanId(detailPlanId))
			.fetch();
	}

	/**
	 * detailPlanEval eq
	 */
	private BooleanExpression detailPlanEvalEqDetailPlanId(Long detailPlanId) {
		return detailPlanId == null ? null : detailPlanEval.detailPlanEvalId.detailPlanId.eq(detailPlanId);
	}

	private BooleanExpression detailPlanEvalEqParticipantId(Long participantId) {
		return participantId == null ? null : detailPlanEval.detailPlanEvalId.participantId.eq(participantId);
	}
}
