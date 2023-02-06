package pcrc.gotbetter.room.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

import static pcrc.gotbetter.room.data_access.entity.QRoom.room;
import static pcrc.gotbetter.room.data_access.entity.QUserRoom.userRoom;

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
                .rightJoin(room)
                .where(userRoom.roomId.eq(room.roomId), userRoom.userId.eq(user_id))
                .fetch();
    }

    @Override
    public Boolean existsByUserIdAndRoomId(Long user_id, Long room_id) {
        Integer exists = queryFactory
                .selectOne()
                .from(userRoom)
                .where(eqUserId(user_id), eqRoomId(room_id))
                .fetchFirst();
        return exists != null;
    }

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


}
