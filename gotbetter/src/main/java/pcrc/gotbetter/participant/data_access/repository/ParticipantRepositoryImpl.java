package pcrc.gotbetter.participant.data_access.repository;

import static com.querydsl.jpa.JPAExpressions.*;
import static pcrc.gotbetter.detail_plan.data_access.entity.QDetailPlan.*;
import static pcrc.gotbetter.participant.data_access.entity.QParticipant.*;
import static pcrc.gotbetter.room.data_access.entity.QRoom.*;
import static pcrc.gotbetter.user.data_access.entity.QUser.*;
import static pcrc.gotbetter.user.data_access.entity.QUserSet.*;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;

public class ParticipantRepositoryImpl implements ParticipantQueryRepository {

	private final JPAQueryFactory queryFactory;

	public ParticipantRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Boolean isMatchedLeader(Long userId, Long roomId) {
		Integer exists = queryFactory
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
		Integer exists = queryFactory
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
				participant, user, userSet.authId))
			.from(participant)
			.leftJoin(user).on(participant.userId.eq(user.userId)).fetchJoin()
			.leftJoin(userSet).on(participant.userId.eq(userSet.userId)).fetchJoin()
			.where(participantEqRoomId(roomId))
			.fetch();
	}

	@Override
	public Boolean existsByDetailPlanId(Long userId, Long detailPlanId) {
		Integer exists = queryFactory
			.selectOne()
			.from(participant)
			.where(
				participant.roomId.eq(
					select(detailPlan.participantInfo.roomId)
						.from(detailPlan)
						.where(detailPlan.detailPlanId.eq(detailPlanId))
				),
				participantEqUserId(userId))
			.fetchFirst();
		return exists != null;
	}

	@Override
	public ParticipantDto findParticipantRoomByParticipantId(Long participantId) {
		return queryFactory
			.select(Projections.constructor(ParticipantDto.class,
				participant, room))
			.from(participant)
			.leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
			.where(participantEqParticipantId(participantId))
			.fetchFirst();
	}

	@Override
	public List<ParticipantDto> findParticipantRoomByRoomId(Long roomId) {
		return queryFactory
			.select(Projections.constructor(ParticipantDto.class,
				participant, room, user))
			.from(participant)
			.leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
			.leftJoin(user).on(participant.userId.eq(user.userId)).fetchJoin()
			.where(participantEqRoomId(roomId))
			.fetch();
	}

	@Override
	public ParticipantDto findParticipantByUserIdAndRoomId(Long userId, Long roomId) {
		return queryFactory
			.select(Projections.constructor(ParticipantDto.class,
				participant, room))
			.from(participant)
			.leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
			.where(participantEqUserId(userId),
				participantEqRoomId(roomId))
			.fetchFirst();
	}

	@Override
	public List<ParticipantDto> findRoomsWithLeader() {
		return queryFactory
			.select(Projections.constructor(ParticipantDto.class,
				participant, room, user))
			.from(participant)
			.leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
			.leftJoin(user).on(participant.userId.eq(user.userId)).fetchJoin()
			.where(participant.authority.eq(true))
			.fetch();
	}

	@Override
	public List<ParticipantDto> findRoomsByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(ParticipantDto.class,
				participant, room, user))
			.from(participant)
			.leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
			.leftJoin(user).on(participant.userId.eq(user.userId)).fetchJoin()
			.where(participantEqUserId(userId))
			.fetch();
	}

	@Override
	public ParticipantDto findRoomWithLeaderByRoomId(Long roomId) {
		return queryFactory
			.select(Projections.constructor(ParticipantDto.class,
				participant, room, user))
			.from(participant)
			.leftJoin(room).on(participant.roomId.eq(room.roomId)).fetchJoin()
			.leftJoin(user).on(participant.userId.eq(user.userId)).fetchJoin()
			.where(participant.authority.eq(true), participantEqRoomId(roomId))
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