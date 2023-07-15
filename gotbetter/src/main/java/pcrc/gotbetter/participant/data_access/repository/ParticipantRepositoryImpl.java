package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import static pcrc.gotbetter.participant.data_access.entity.QJoinRequest.joinRequest;
import static pcrc.gotbetter.participant.data_access.entity.QParticipant.participant;

public class ParticipantRepositoryImpl implements ParticipantRepositoryQueryDSL {

    private final JPAQueryFactory queryFactory;

    public ParticipantRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void updateParticipateAccepted(Long userId, Long roomId) {
        queryFactory
                .update(joinRequest)
                .where(joinRequestEqRoomId(roomId), joinRequestEqUserId(userId))
                .set(joinRequest.accepted, true)
                .execute();
    }

    @Override
    @Transactional
    public void updatePercentSum(Long participantId, Float percent) {
        queryFactory
                .update(participant)
                .where(participantEqParticipantId(participantId))
                .set(participant.percentSum, participant.percentSum.add(percent))
                .execute();
    }

    @Override
    @Transactional
    public void updateRefund(Long participantId, Integer refund) {
        queryFactory
                .update(participant)
                .where(participantEqParticipantId(participantId))
                .set(participant.refund, refund)
                .execute();
    }

    @Override
    public Boolean isMatchedLeader(Long userId, Long roomId) {
        Integer exists =  queryFactory
                .selectOne()
                .from(participant)
                .where(participantEqUserId(userId),
                        participantEqRoomId(roomId),
                        participant.authority.eq(true))
                .fetchFirst();
        return exists != null;
    }

    /**
     * participant eq
     */
    private BooleanExpression participantEqUserId(Long userId) {
        return userId == null ? null : participant.userId.eq(userId);
    }

    private BooleanExpression participantEqRoomId(Long roomId) {
        return roomId == null ? null : participant.roomId.eq(roomId);
    }

    private BooleanExpression participantEqParticipantId(Long participantId) {
        return participantId == null ? null : participant.participantId.eq(participantId);
    }

    /**
     * participate eq
     */
    private BooleanExpression joinRequestEqUserId(Long userId) {
        return userId == null ? null : joinRequest.joinRequestId.userId.eq(userId);
    }

    private BooleanExpression joinRequestEqRoomId(Long roomId) {
        return roomId == null ? null : joinRequest.joinRequestId.roomId.eq(roomId);
    }
}