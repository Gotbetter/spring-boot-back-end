package pcrc.gotbetter.participant.data_access.repository;

import java.util.List;

import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;

public interface ParticipantQueryRepository {
    void updatePercentSum(Long participantId, Float percent); /** Todo batchconfig */
    void updateRefund(Long participantId, Integer refund); /** Todo batchconfig */

    Boolean isMatchedLeader(Long userId, Long roomId);

    Participant findByUserIdAndRoomId(Long userId, Long roomId);

    Boolean existsByUserIdAndRoomId(Long userId, Long roomId);

    List<ParticipantDto> findUserInfoList(Long roomId);

    ParticipantDto findParticipantRoom(Long participantId);
}