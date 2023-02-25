package pcrc.gotbetter.user.data_access.repository;

import com.querydsl.core.BooleanBuilder;
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
                .set(user.refresh_token, refresh_token)
                .execute();
    }

    @Override
    public Boolean existsByAuthidOrEmail(String auth_id, String email) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(eqAuthId(auth_id))
                .or(eqEmail(email));
        Integer existsUser = queryFactory
                .selectOne()
                .from(user)
                .where(builder)
                .fetchFirst();
        return existsUser != null;
    }

    /**
     * user eq
     */
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
