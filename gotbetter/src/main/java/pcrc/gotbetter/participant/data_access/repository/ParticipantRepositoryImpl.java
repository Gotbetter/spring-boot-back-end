package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.user.data_access.entity.User;

import java.util.List;

import static pcrc.gotbetter.participant.data_access.entity.QParticipant.participant;
import static pcrc.gotbetter.participant.data_access.entity.QParticipate.participate;
import static pcrc.gotbetter.user.data_access.entity.QUser.user;

public class ParticipantRepositoryImpl implements ParticipantRepositoryQueryDSL {

    private final JPAQueryFactory queryFactory;

    public ParticipantRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsWaitMemberInARoom(Long user_id, Long room_id, Boolean active) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(participateEqUserId(user_id))
                .and(participateEqRoomId(room_id))
                .and(participateEqAccepted(false));
        Integer exists =  queryFactory
                .selectOne()
                .from(participate)
                .where(builder)
                .fetchFirst();
        return exists != null;
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
    public Participant findByUserIdAndRoomId(Long user_id, Long room_id) {
        return queryFactory
                .select(participant)
                .from(participant)
                .where(participantEqUserId(user_id), participantEqRoomId(room_id))
                .fetchFirst();
    }

    @Override
    public List<User> findWaitMembers(Long room_id) {
        return queryFactory
                .select(user)
                .from(user)
                .where(user.userId.in(
                        JPAExpressions
                                .select(participate.participateId.userId)
                                .from(participate)
                                .where(participateEqRoomId(room_id), participateEqAccepted(false))
                ))
                .fetch();
    }

    @Override
    public List<Tuple> findActiveMembers(Long room_id) {
        return queryFactory
                .select(participant.participantId, user.userId, user.authId,
                        user.usernameNick, user.email, user.profile)
                .from(participant)
                .leftJoin(user)
                .where(participant.userId.eq(user.userId), participantEqRoomId(room_id))
                .fetch();
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

    @Override
    public Boolean existsMemberInRoom(Long user_id, Long room_id) {
        Integer exists =  queryFactory
                .selectOne()
                .from(participant)
                .where(participantEqUserId(user_id), participantEqRoomId(room_id))
                .fetchFirst();
        return exists != null;
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

    /**
     * participate eq
     */
    private BooleanExpression participateEqUserId(Long user_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(user_id))) {
            return null;
        }
        return participate.participateId.userId.eq(user_id);
    }

    private BooleanExpression participateEqRoomId(Long room_id) {
        if (StringUtils.isNullOrEmpty(String.valueOf(room_id))) {
            return null;
        }
        return participate.participateId.roomId.eq(room_id);
    }

    private BooleanExpression participateEqAccepted(Boolean accepted) {
        if (StringUtils.isNullOrEmpty(String.valueOf(accepted))) {
            return null;
        }
        return participate.accepted.eq(accepted);
    }
}