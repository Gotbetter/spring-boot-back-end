package pcrc.gotbetter.room.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

import static pcrc.gotbetter.room.data_access.entity.QRoom.room;
import static pcrc.gotbetter.user_room.data_access.entity.QUserRoom.userRoom;

public class RoomRepositoryQueryDSLImpl implements RoomRepositoryQueryDSL{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public RoomRepositoryQueryDSLImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Room> findUserRooms(Long user_id) {
        return queryFactory
                .select(room)
                .from(userRoom)
                .leftJoin(room)
                .where(userRoom.roomId.eq(room.roomId), eqUserId(user_id), eqAccepted(true))
                .fetch();
    }

    @Override
    public Room findRoomWithUserIdAndRoomId(Long user_id, Long room_id) {
        return queryFactory
                .select(room)
                .from(room)
                .where(room.roomId.eq(JPAExpressions
                        .select(userRoom.roomId)
                        .from(userRoom)
                        .where(eqRoomId(room_id), eqUserId(user_id), eqAccepted(true))))
                .fetchFirst();
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
