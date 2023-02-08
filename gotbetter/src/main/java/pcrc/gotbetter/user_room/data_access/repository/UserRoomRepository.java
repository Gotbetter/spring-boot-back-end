package pcrc.gotbetter.user_room.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.user_room.data_access.entity.UserRoom;

import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long>, UserRoomRepositoryQueryDSL {
    List<UserRoom> findByRoomId(Long room_id);
}
