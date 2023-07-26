package pcrc.gotbetter.participant.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.participant.data_access.entity.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, ParticipantQueryRepository {

	Participant findByParticipantId(Long participantId);

}
