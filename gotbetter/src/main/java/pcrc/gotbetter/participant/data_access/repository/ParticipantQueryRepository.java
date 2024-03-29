package pcrc.gotbetter.participant.data_access.repository;

import java.util.List;

import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;

public interface ParticipantQueryRepository {

	Boolean isMatchedLeader(Long userId, Long roomId);

	Participant findLeaderByRoomId(Long roomId);

	Participant findByUserIdAndRoomId(Long userId, Long roomId);

	Boolean existsByUserIdAndRoomId(Long userId, Long roomId);

	List<ParticipantDto> findUserInfoList(Long roomId);

	Boolean existsByDetailPlanId(Long userId, Long detailPlanId);

	// join
	ParticipantDto findParticipantRoomByParticipantId(Long participantId);

	List<ParticipantDto> findParticipantRoomByRoomId(Long roomId);

	ParticipantDto findParticipantByUserIdAndRoomId(Long userId, Long roomId);

	List<ParticipantDto> findRoomsWithLeader();

	List<ParticipantDto> findRoomsByUserId(Long userId);

	ParticipantDto findRoomWithLeaderByRoomId(Long roomId);

	List<ParticipantDto> findMembersByPlanId(Long planId);

	List<ParticipantDto> findMembersByDetailId(Long detailPlanId);

}