package pcrc.gotbetter.participant.data_access.repository;

public interface ParticipantQueryRepository {
    void updateParticipateAccepted(Long userId, Long roomId);
    void updatePercentSum(Long participantId, Float percent); // batchconfig
    void updateRefund(Long participantId, Integer refund); // batchconfig

    Boolean isMatchedLeader(Long userId, Long roomId);

}