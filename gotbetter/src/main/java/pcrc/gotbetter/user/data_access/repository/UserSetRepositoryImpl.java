package pcrc.gotbetter.user.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static pcrc.gotbetter.user.data_access.entity.QUserSet.userSet;

public class UserSetRepositoryImpl implements UserSetRepositoryQueryDSL {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserSetRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsByUserId(Long userId) {
        Integer existsUser = queryFactory
                .selectOne()
                .from(userSet)
                .where(eqUserId(userId))
                .fetchFirst();
        return existsUser != null;
    }

    @Override
    public Boolean existsByAuthId(String authId) {
        Integer existsUser = queryFactory
                .selectOne()
                .from(userSet)
                .where(eqAuthId(authId))
                .fetchFirst();
        return existsUser != null;
    }

    @Override
    public String findAuthIdByUserId(Long userId) {
        return queryFactory
                .select(userSet.authId)
                .from(userSet)
                .where(eqUserId(userId))
                .fetchFirst();
    }

    /**
     * userSet eq
     */
    private BooleanExpression eqUserId(Long userId) {
        return userId == null ? null : userSet.userId.eq(userId);
    }

    private BooleanExpression eqAuthId(String authId) {
        if (StringUtils.isNullOrEmpty(authId)) {
            return null;
        }
        return userSet.authId.eq(authId);
    }

}
