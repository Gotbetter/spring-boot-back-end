package pcrc.gotbetter.user_room.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import pcrc.gotbetter.user.data_access.entity.User;

import java.util.List;

import static pcrc.gotbetter.room.data_access.entity.QRoom.room;
import static pcrc.gotbetter.user.data_access.entity.QUser.user;
import static pcrc.gotbetter.user_room.data_access.entity.QUserRoom.userRoom;

public class UserRoomRepositoryQueryDSLImpl implements UserRoomRepositoryQueryDSL {

    private final JPAQueryFactory queryFactory;

    public UserRoomRepositoryQueryDSLImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsRoomMatchLeaderId(Long leader_id, Long room_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(room)
                .where(room.roomId.eq(room_id), room.leaderId.eq(leader_id))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public Boolean existsActiveMemberInARoom(Long room_id, Long user_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(userRoom)
                .where(eqRoomId(room_id), eqUserId(user_id), eqAccepted(true))
                .fetchFirst();
        return exists != null;
    }

    @Override
    public List<User> findMembersInARoom(Long room_id, Boolean accepted) {
        return queryFactory
                .select(user)
                .from(user)
                .where(user.id.in(
                        JPAExpressions
                                .select(userRoom.userId)
                                .from(userRoom)
                                .where(eqRoomId(room_id), eqAccepted(accepted))
                ))
                .fetch();
    }

    /**
     * user room eq
     */
    private BooleanExpression eqUserId(Long user_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(user_id))) {
            return null;
        }
        return userRoom.userId.eq(user_id);
    }

    private BooleanExpression eqRoomId(Long room_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_id))) {
            return null;
        }
        return userRoom.roomId.eq(room_id);
    }

    private BooleanExpression eqAccepted(Boolean accepted) {
        if (StringUtils.isNullOrEmpty(String.valueOf(accepted))) {
            return null;
        }
        return userRoom.accepted.eq(accepted);
    }
}
