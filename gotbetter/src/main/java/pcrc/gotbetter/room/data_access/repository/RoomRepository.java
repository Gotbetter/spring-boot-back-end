package pcrc.gotbetter.room.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryQueryDSL {
    Optional<Room> findByRoomCode(String roomCode);
    Optional<Room> findByRoomId(Long roomId);
}
