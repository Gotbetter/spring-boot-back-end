package pcrc.gotbetter.participant.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pcrc.gotbetter.participant.data_access.entity.Participate;

import java.util.List;

public interface ParticipateRepository extends JpaRepository<Participate, Long> {
    List<Participate> findByRoomId(Long room_id);
}
