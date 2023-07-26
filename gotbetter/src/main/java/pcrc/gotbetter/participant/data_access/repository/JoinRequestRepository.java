package pcrc.gotbetter.participant.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, JoinRequestId>, JoinRequestQueryRepository {

	Optional<JoinRequest> findByJoinRequestId(JoinRequestId joinRequestId);

}
