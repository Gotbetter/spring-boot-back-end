package pcrc.gotbetter.user.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.user.data_access.entity.User;

import static pcrc.gotbetter.user.data_access.entity.QUser.user;

public class UserRepositoryImpl implements UserRepositoryQueryDSL {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        queryFactory
                .update(user)
                .where(eqUserId(userId))
                .set(user.refreshToken, refreshToken)
                .execute();
    }

    @Override
    @Transactional
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

    @Override
    public User findByEmail(String email) {
        return queryFactory
                .selectFrom(user)
                .where(eqEmail(email))
                .fetchFirst();
    }

    /**
     * user eq
     */
    private BooleanExpression eqUserId(Long userId) {
        return userId == null ? null : user.userId.eq(userId);
    }

    private BooleanExpression eqEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            return null;
        }
        return user.email.eq(email);
    }
}
