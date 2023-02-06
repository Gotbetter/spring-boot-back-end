package pcrc.gotbetter.room.data_access.repository;

import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

public interface RoomRepositoryQueryDSL {
    List<Room> findUserRooms(Long user_id);
    Boolean existsByUserIdAndRoomId(Long user_id, Long room_id);
}
