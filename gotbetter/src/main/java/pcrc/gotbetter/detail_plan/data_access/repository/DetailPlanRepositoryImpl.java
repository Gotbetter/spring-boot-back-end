package pcrc.gotbetter.detail_plan.data_access.repository;

import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.*;
import static pcrc.gotbetter.room.data_access.entity.QRoom.*;

import java.util.HashMap;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import pcrc.gotbetter.detail_plan.data_access.dto.DetailPlanDto;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

public class DetailPlanRepositoryImpl implements DetailPlanQueryRepository {
	private final JPAQueryFactory queryFactory;
	private final EntityManager em;

	public DetailPlanRepositoryImpl(JPAQueryFactory queryFactory, EntityManager em) {
		this.queryFactory = queryFactory;
		this.em = em;
	}

	@Override
	public Boolean existsByPlanId(Long planId) {
		Integer exists = queryFactory
			.selectOne()
			.from(detailPlan)
			.where(detailPlanEqPlanId(planId))
			.fetchFirst();
		return exists != null;
	}

	@Override
	public HashMap<String, Long> countCompleteTrue(Long planId) {
		Long size = (long)queryFactory.selectFrom(detailPlan)
			.where(detailPlan.planId.eq(planId))
			.fetch().size();

		Long completeCount = (long)queryFactory.selectFrom(detailPlan)
			.where(detailPlan.planId.eq(planId).and(detailPlan.complete.isTrue()))
			.fetch().size();

		HashMap<String, Long> result = new HashMap<>();

		result.put("size", size);
		result.put("completeCount", completeCount);
		return result;
	}

	@Override
	public DetailPlan findByDetailPlanId(Long detailPlanId) {
		return queryFactory
			.selectFrom(detailPlan)
			.where(detailPlanEqDetailPlanId(detailPlanId))
			.fetchFirst();
	}

	@Override
	public DetailPlanDto findByDetailJoinRoom(Long detailPlanId) {
		return queryFactory
			.select(Projections.constructor(DetailPlanDto.class, detailPlan, room))
			.from(detailPlan)
			.leftJoin(room).on(detailPlan.participantInfo.roomId.eq(room.roomId)).fetchJoin()
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
