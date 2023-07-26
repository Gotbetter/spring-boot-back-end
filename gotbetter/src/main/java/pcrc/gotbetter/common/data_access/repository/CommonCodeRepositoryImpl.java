package pcrc.gotbetter.common.data_access.repository;

import static pcrc.gotbetter.common.data_access.entity.QCommonCode.*;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;

public class CommonCodeRepositoryImpl implements CommonCodeQueryRepository {
	private final JPAQueryFactory queryFactory;

	public CommonCodeRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public CommonCode findByCommonCodeId(CommonCodeId commonCodeId) {
		return queryFactory
			.selectFrom(commonCode)
			.where(eqGroupCode(commonCodeId.getGroupCode()),
				eqCode(commonCodeId.getCode()))
			.fetchFirst();
	}

	@Override
	public List<CommonCode> findListByGroupCode(String groupCode) {
		return queryFactory
			.selectFrom(commonCode)
			.where(eqGroupCode(groupCode))
			.orderBy(commonCode.order.asc())
			.fetch();
	}

	/**
	 * eq
	 */
	private BooleanExpression eqGroupCode(String groupCode) {
		if (StringUtils.isNullOrEmpty(String.valueOf(groupCode))) {
			return null;
		}
		return commonCode.commonCodeId.groupCode.eq(groupCode);
	}

	private BooleanExpression eqCode(String code) {
		if (StringUtils.isNullOrEmpty(String.valueOf(code))) {
			return null;
		}
		return commonCode.commonCodeId.code.eq(code);
	}
}
