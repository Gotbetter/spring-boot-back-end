package pcrc.gotbetter.participant.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;

import java.util.Optional;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, JoinRequestId> {
    Optional<JoinRequest> findByParticipateId(JoinRequestId joinRequestId);
}
