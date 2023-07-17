package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static pcrc.gotbetter.participant.data_access.entity.QJoinRequest.joinRequest;
import static pcrc.gotbetter.room.data_access.entity.QRoom.room;
import static pcrc.gotbetter.user.data_access.entity.QUser.user;
import static pcrc.gotbetter.user.data_access.entity.QUserSet.*;

public class JoinRequestRepositoryImpl implements JoinRequestQueryRepository {

    private final JPAQueryFactory queryFactory;

    public JoinRequestRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public JoinRequestDto findJoinRequest(Long userId, Long roomId, Boolean accepted) {
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
    public List<JoinRequestDto> findJoinRequestList(Long userId, Long roomId, Boolean accepted) {
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