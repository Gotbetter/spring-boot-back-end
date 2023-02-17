package pcrc.gotbetter.room.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

import static pcrc.gotbetter.participant.data_access.entity.QParticipant.participant;
import static pcrc.gotbetter.room.data_access.entity.QRoom.room;

public class RoomRepositoryImpl implements RoomRepositoryQueryDSL{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public RoomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Room> findUserRooms(Long user_id) {
        return queryFactory
                .select(room)
                .from(room)
                .leftJoin(participant)
                .where(room.roomId.eq(participant.roomId), participantEqUserId(user_id))
                .fetch();
    }

    @Override
    public Room findRoomWithUserIdAndRoomId(Long user_id, Long room_id) {
        return queryFactory
                .select(room)
                .from(room)
                .where(room.roomId.eq(JPAExpressions
                        .select(participant.roomId)
                        .from(participant)
                        .where(participantEqRoomId(room_id), participantEqUserId(user_id))))
                .fetchFirst();
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
        if (StringUtils.isNullOrEmpty(String.valueOf(room_id))) {
            return null;
        }
        return room.roomId.eq(room_id);
    }

    private BooleanExpression roomEqRoomCode(String room_code) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_code))) {
            return null;
        }
        return room.roomCode.eq(room_code);
    }
    /**
     * participant eq
     */
    private BooleanExpression participantEqUserId(Long user_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(user_id))) {
            return null;
        }
        return participant.userId.eq(user_id);
    }

    private BooleanExpression participantEqRoomId(Long room_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_id))) {
            return null;
        }
        return participant.roomId.eq(room_id);
    }
}
