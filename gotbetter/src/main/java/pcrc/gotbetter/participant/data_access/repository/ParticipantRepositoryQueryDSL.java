package pcrc.gotbetter.participant.data_access.repository;

public interface ParticipantRepositoryQueryDSL {
    // insert, update, delete
    void updateParticipateAccepted(Long user_id, Long room_id);

    // select
    Boolean isMatchedLeader(Long user_id, Long room_id);
}