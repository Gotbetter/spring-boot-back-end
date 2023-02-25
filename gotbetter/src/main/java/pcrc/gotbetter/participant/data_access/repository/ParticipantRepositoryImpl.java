package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import static pcrc.gotbetter.participant.data_access.entity.QParticipant.participant;
import static pcrc.gotbetter.participant.data_access.entity.QParticipate.participate;

public class ParticipantRepositoryImpl implements ParticipantRepositoryQueryDSL {

    private final JPAQueryFactory queryFactory;

    public ParticipantRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateParticipateAccepted(Long user_id, Long room_id) {
        queryFactory
                .update(participate)
                .where(participateEqRoomId(room_id), participateEqUserId(user_id))
                .set(participate.accepted, true)
                .execute();
    }

    @Override
    public Boolean isMatchedLeader(Long user_id, Long room_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(participant)
                .where(participantEqUserId(user_id), participantEqRoomId(room_id),
                        participant.authority.eq(true))
                .fetchFirst();
        return exists != null;
    }

    /**
     * participant eq
     */
    private BooleanExpression participantEqUserId(Long user_id) {
        return user_id == null ? null : participant.userId.eq(user_id);
    }

    private BooleanExpression participantEqRoomId(Long room_id) {
        return room_id == null ? null : participant.roomId.eq(room_id);
    }

    /**
     * participate eq
     */
    private BooleanExpression participateEqUserId(Long user_id) {
        return user_id == null ? null : participate.participateId.userId.eq(user_id);
    }

    private BooleanExpression participateEqRoomId(Long room_id) {
        return room_id == null ? null : participate.participateId.roomId.eq(room_id);
    }
}