package pcrc.gotbetter.participant.data_access.repository;

import pcrc.gotbetter.participant.data_access.entity.Participant;

import java.util.List;

public interface ParticipantRepositoryQueryDSL {
    // insert, update, delete
    void updateParticipateAccepted(Long user_id, Long room_id);
    void updatePercentSum(Long participant_id, Float percent);
    void updateRefund(Long participant_id, Integer refund);

    // select
    Boolean isMatchedLeader(Long user_id, Long room_id);
    List<Participant> findListByRoomId(Long room_id);
}