package pcrc.gotbetter.participant.data_access.repository;

public interface ParticipantRepositoryQueryDSL {
    // insert, update, delete
    void updateParticipateAccepted(Long userId, Long roomId);
    void updatePercentSum(Long participantId, Float percent);
    void updateRefund(Long participantId, Integer refund);

    // select
    Boolean isMatchedLeader(Long userId, Long roomId);
}