package pcrc.gotbetter.participant.data_access.repository;

import pcrc.gotbetter.participant.data_access.entity.Participant;

public interface ParticipantRepositoryQueryDSL {
    // insert, update, delete
    void updateParticipateAccepted(Long user_id, Long room_id);

    // select
    Boolean isMatchedLeader(Long user_id, Long room_id);
}