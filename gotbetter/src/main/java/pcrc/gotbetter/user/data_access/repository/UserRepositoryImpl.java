package pcrc.gotbetter.user.data_access.repository;

import static pcrc.gotbetter.user.data_access.entity.QUser.*;
import static pcrc.gotbetter.user.data_access.entity.QUserSet.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.user.data_access.dto.UserDto;

public class UserRepositoryImpl implements UserQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Autowired
	public UserRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
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

	@Override
	public List<UserDto> findAllUserUserSet() {
		return queryFactory
			.select(Projections.constructor(UserDto.class, user, userSet))
			.from(user)
			.leftJoin(userSet).on(user.userId.eq(userSet.userId)).fetchJoin()
			.fetch();
	}

	/**
	 * user eq
	 */
	private BooleanExpression eqUserId(Long userId) {
		return userId == null ? null : user.userId.eq(userId);
	}
}
