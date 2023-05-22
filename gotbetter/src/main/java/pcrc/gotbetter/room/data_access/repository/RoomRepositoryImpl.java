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
    public void updatePlusTotalEntryFeeAndCurrentNum(Long roomId, Integer fee) {
        queryFactory
                .update(room)
                .set(room.totalEntryFee, room.totalEntryFee.add(fee))
                .set(room.currentUserNum, room.currentUserNum.add(1))
                .where(roomEqRoomId(roomId))
                .execute();
    }

    @Override
    @Transactional
    public void updateCurrentWeek(Long roomId, Integer changeWeek) {
        queryFactory
                .update(room)
                .set(room.currentWeek, changeWeek)
                .where(roomEqRoomId(roomId))
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
    public List<Room> findListLastWeek() {
        return queryFactory
                .selectFrom(room)
                .where(room.week.eq(room.currentWeek))
                .fetch();
    }

    @Override
    public Integer findCurrentWeek(Long roomId) {
        return queryFactory
                .select(room.currentWeek)
                .from(room)
                .where(roomEqRoomId(roomId))
                .fetchFirst();
    }

    @Override
    public Boolean existByRoomCode(String roomCode) {
        Integer exists =  queryFactory
                .selectOne()
                .from(room)
                .where(roomEqRoomCode(roomCode))
                .fetchFirst();
        return exists != null;
    }

    /**
     * room eq
     */
    private BooleanExpression roomEqRoomId(Long roomId) {
        return roomId == null ? null : room.roomId.eq(roomId);
    }

    private BooleanExpression roomEqRoomCode(String roomCode) {
        if (StringUtils.isNullOrEmpty(String.valueOf(roomCode))) {
            return null;
        }
        return room.roomCode.eq(roomCode);
    }
}
