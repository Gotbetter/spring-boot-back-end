package pcrc.gotbetter.room.data_access.repository;

import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.user.data_access.domain.User;

import java.util.List;

public interface RoomRepositoryQueryDSL {
    List<Room> findUserRooms(Long user_id);
    Room findRoomWithUserIdAndRoomId(Long user_id, Long room_id);
    List<User> findWaitUsersByRoomId(Long room_id);
    Boolean existsRoomMatchLeaderId(Long leader_id, Long room_id);
}
