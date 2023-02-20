package pcrc.gotbetter.participant.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pcrc.gotbetter.participant.data_access.entity.Participate;
import pcrc.gotbetter.participant.data_access.entity.ParticipateId;

import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, ParticipateId> {
    Optional<Participate> findByParticipateId(ParticipateId participateId);
}
