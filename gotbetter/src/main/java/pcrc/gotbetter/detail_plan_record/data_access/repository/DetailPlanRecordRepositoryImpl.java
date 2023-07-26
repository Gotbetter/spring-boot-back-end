package pcrc.gotbetter.detail_plan_record.data_access.repository;

import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.*;
import static pcrc.gotbetter.detail_plan_record.data_access.entity.QDetailPlanRecord.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.detail_plan_record.data_access.dto.DetailPlanRecordDto;

public class DetailPlanRecordRepositoryImpl implements DetailPlanRecordQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Autowired
	public DetailPlanRecordRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public DetailPlanRecordDto findDetailPlanJoinRecord(Long recordId) {
		return queryFactory
			.select(Projections.constructor(DetailPlanRecordDto.class, detailPlanRecord, detailPlan))
			.from(detailPlanRecord)
			.leftJoin(detailPlan)
			.on(detailPlanRecord.detailPlanId.detailPlanId.eq(detailPlan.detailPlanId))
			.fetchJoin()
			.where(eqRecordId(recordId))
			.fetchFirst();
	}

	private BooleanExpression eqRecordId(Long recordId) {
		return recordId == null ? null : detailPlanRecord.recordId.eq(recordId);
	}
}
