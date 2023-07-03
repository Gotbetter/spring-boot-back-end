package pcrc.gotbetter.common.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;

import java.util.List;

import static pcrc.gotbetter.common.data_access.entity.QCommonCode.commonCode;

public class CommonCodeRepositoryImpl implements CommonCodeRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    public CommonCodeRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsByCommonCodeId(CommonCodeId commonCodeId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(commonCode)
                .where(eqCommonCodeId(commonCodeId))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public CommonCode findRoomCategoryInfo(String roomCategory) {
        return queryFactory
                .selectFrom(commonCode)
                .where(eqGroupCode("ROOM_CATEGORY"), eqCode(roomCategory))
                .fetchFirst();
    }

    @Override
    public List<CommonCode> findRoomCategories() {
        return queryFactory
                .selectFrom(commonCode)
                .where(eqGroupCode("ROOM_CATEGORY"))
                .orderBy(commonCode.order.asc())
                .fetch();
    }

    /**
     * eq
     */
    private BooleanExpression eqCommonCodeId(CommonCodeId commonCodeId) {
        if (StringUtils.isNullOrEmpty(String.valueOf(commonCodeId.getGroupCode()))
                || StringUtils.isNullOrEmpty(String.valueOf(commonCodeId.getCode()))) {
            return null;
        }
        return commonCode.commonCodeId.eq(commonCodeId);
    }

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
