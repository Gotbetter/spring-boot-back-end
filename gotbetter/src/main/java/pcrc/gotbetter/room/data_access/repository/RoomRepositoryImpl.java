package pcrc.gotbetter.room.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

import static pcrc.gotbetter.room.data_access.entity.QRoom.room;

public class RoomRepositoryImpl implements RoomRepositoryQueryDSL{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public RoomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updatePlusTotalEntryFeeAndCurrentNum(Long room_id, Integer fee) {
        queryFactory
                .update(room)
                .set(room.totalEntryFee, room.totalEntryFee.add(fee))
                .set(room.currentUserNum, room.currentUserNum.add(1))
                .where(roomEqRoomId(room_id))
                .execute();
    }

    @Override
    @Transactional
    public void updateCurrentWeek(Long room_id, Integer plusWeek) {
        queryFactory
                .update(room)
                .set(room.currentWeek, room.currentWeek.add(plusWeek))
                .where(roomEqRoomId(room_id))
                .execute();
    }

    @Override
    public List<Room> findListUnderWeek() {
        return queryFactory
                .selectFrom(room)
                .where(room.week.gt(room.currentWeek))
                .fetch();
    }

    @Override
    public Integer findCurrentWeek(Long room_id) {
        return queryFactory
                .select(room.currentWeek)
                .from(room)
                .where(roomEqRoomId(room_id))
                .fetchFirst();
    }

    @Override
    public Boolean existByRoomCode(String room_code) {
        Integer exists =  queryFactory
                .selectOne()
                .from(room)
                .where(roomEqRoomCode(room_code))
                .fetchFirst();
        return exists != null;
    }

    /**
     * room eq
     */
    private BooleanExpression roomEqRoomId(Long room_id) {
        return room_id == null ? null : room.roomId.eq(room_id);
    }

    private BooleanExpression roomEqRoomCode(String room_code) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_code))) {
            return null;
        }
        return room.roomCode.eq(room_code);
    }
}
