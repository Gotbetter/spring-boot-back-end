package pcrc.gotbetter.participant.data_access.repository;

import static pcrc.gotbetter.participant.data_access.entity.QJoinRequest.*;
import static pcrc.gotbetter.room.data_access.entity.QRoom.*;
import static pcrc.gotbetter.user.data_access.entity.QUser.*;
import static pcrc.gotbetter.user.data_access.entity.QUserSet.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;

public class JoinRequestRepositoryImpl implements JoinRequestQueryRepository {

	private final JPAQueryFactory queryFactory;

	public JoinRequestRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public JoinRequest findJoinRequest(Long userId, Long roomId) {
		return queryFactory
			.selectFrom(joinRequest)
			.where(eqUserId(userId),
				eqRoomId(roomId),
				eqAccepted(false))
			.fetchFirst();
	}

	@Override
	public JoinRequestDto findJoinRequestJoin(Long userId, Long roomId, Boolean accepted) {
		return queryFactory
			.select(Projections.constructor(JoinRequestDto.class,
				joinRequest, room, user, userSet))
			.from(joinRequest)
			.leftJoin(room).on(joinRequest.joinRequestId.roomId.eq(room.roomId)).fetchJoin()
			.leftJoin(user).on(joinRequest.joinRequestId.userId.eq(user.userId)).fetchJoin()
			.leftJoin(userSet).on(joinRequest.joinRequestId.userId.eq(userSet.userId)).fetchJoin()
			.where(joinRequest.joinRequestId.userId.eq(userId),
				joinRequest.joinRequestId.roomId.eq(roomId),
				joinRequest.accepted.eq(accepted))
			.fetchFirst();
	}

	@Override
	public List<JoinRequestDto> findJoinRequestJoinList(Long userId, Long roomId, Boolean accepted) {
		return queryFactory
			.select(Projections.constructor(JoinRequestDto.class,
				joinRequest, room, user, userSet))
			.from(joinRequest)
			.leftJoin(room).on(joinRequest.joinRequestId.roomId.eq(room.roomId)).fetchJoin()
			.leftJoin(user).on(joinRequest.joinRequestId.userId.eq(user.userId)).fetchJoin()
			.leftJoin(userSet).on(joinRequest.joinRequestId.userId.eq(userSet.userId)).fetchJoin()
			.where(eqUserId(userId),
				eqRoomId(roomId),
				eqAccepted(accepted))
			.fetch();
	}

	private BooleanExpression eqUserId(Long userId) {
		return userId == null ? null : joinRequest.joinRequestId.userId.eq(userId);
	}

	private BooleanExpression eqRoomId(Long roomId) {
		return roomId == null ? null : joinRequest.joinRequestId.roomId.eq(roomId);
	}

	private BooleanExpression eqAccepted(Boolean accepted) {
		return accepted == null ? null : joinRequest.accepted.eq(accepted);
	}
}