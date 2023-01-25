package pcrc.gotbetter.room.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.room.data_access.entity.UserRoom;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
}
