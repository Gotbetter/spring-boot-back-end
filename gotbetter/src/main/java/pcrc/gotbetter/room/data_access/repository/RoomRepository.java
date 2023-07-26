package pcrc.gotbetter.room.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.room.data_access.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, RoomQueryRepository {

	Optional<Room> findByRoomCode(String roomCode);

	Optional<Room> findByRoomId(Long roomId);

}
