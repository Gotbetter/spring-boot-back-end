package pcrc.gotbetter.user.data_access.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.user.data_access.entity.User;

import static pcrc.gotbetter.user.data_access.entity.QUser.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        queryFactory
            .update(user)
            .where(eqUserId(userId))
            .set(user.fcmToken, fcmToken)
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

    @Override
    public HashMap<Long, List<String>> getAllUsersUserIdAndFcmToken() {
        List<Tuple> tuples = queryFactory
            .select(user.userId, user.username, user.fcmToken)
            .from(user)
            .fetch();
        HashMap<Long, List<String>> map = new HashMap<>();
        for (Tuple tuple : tuples) {
            List<String> userInfo = new ArrayList<>();
            userInfo.add(tuple.get(user.username));
            userInfo.add(tuple.get(user.fcmToken));
            map.put(tuple.get(user.userId), userInfo);
        }
        return map;
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
