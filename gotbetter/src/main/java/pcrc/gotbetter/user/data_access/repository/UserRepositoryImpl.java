package pcrc.gotbetter.user.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static pcrc.gotbetter.user.data_access.entity.QUser.user;

public class UserRepositoryImpl implements UserRepositoryQueryDSL {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateRefreshToken(String auth_id, String refresh_token) {
        queryFactory
                .update(user)
                .where(eqAuthId(auth_id))
                .set(user.refreshToken, refresh_token)
                .execute();
    }

    @Override
    public void updateUsername(Long userId, String username) {
        queryFactory
                .update(user)
                .where(eqUserId(userId))
                .set(user.username, username)
                .execute();
    }

    @Override
    public Long findUserIdByEmail(String email) {
        return queryFactory
                .select(user.userId)
                .from(user)
                .where(eqEmail(email))
                .fetchFirst();
    }

    /**
     * user eq
     */
    private BooleanExpression eqUserId(Long userId) {
        return userId == null ? null : user.userId.eq(userId);
    }

    private BooleanExpression eqAuthId(String auth_id) {
        if (StringUtils.isNullOrEmpty(auth_id)) {
            return null;
        }
        return user.authId.eq(auth_id);
    }

    private BooleanExpression eqEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            return null;
        }
        return user.email.eq(email);
    }
}
