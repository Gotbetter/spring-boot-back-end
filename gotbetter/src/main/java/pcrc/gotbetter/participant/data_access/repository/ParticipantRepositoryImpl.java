package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import static pcrc.gotbetter.participant.data_access.entity.QParticipant.participant;
import static pcrc.gotbetter.room.data_access.entity.QRoom.*;
import static pcrc.gotbetter.user.data_access.entity.QUser.user;
import static pcrc.gotbetter.user.data_access.entity.QUserSet.userSet;

import java.util.List;

import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;

public class ParticipantRepositoryImpl implements ParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ParticipantRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
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

    @Override
    public Participant findByUserIdAndRoomId(Long userId, Long roomId) {
        return queryFactory
            .selectFrom(participant)
            .where(participantEqUserId(userId),
                participantEqRoomId(roomId))
            .fetchFirst();
    }

    @Override
    public Boolean existsByUserIdAndRoomId(Long userId, Long roomId) {
        Integer exists =  queryFactory
            .selectOne()
            .from(participant)
            .where(participantEqUserId(userId),
                participantEqRoomId(roomId))
            .fetchFirst();
        return exists != null;
    }

    @Override
    public List<ParticipantDto> findUserInfoList(Long roomId) {
        return queryFactory
            .select(Projections.constructor(ParticipantDto.class,
                participant.participantId, participant.authority,
                user, userSet.authId))
            .from(participant)
            .leftJoin(user).on(participant.userId.eq(user.userId)).fetchJoin()
            .leftJoin(userSet).on(participant.userId.eq(userSet.userId)).fetchJoin()
            .where(participantEqRoomId(roomId))
            .fetch();
    }

    @Override
    public ParticipantDto findParticipantRoom(Long participantId) {
        return queryFactory
            .select(Projections.constructor(ParticipantDto.class,
                participant, room))
            .from(participant)
            .leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
            .where(participantEqParticipantId(participantId))
            .fetchFirst();
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
}