package pcrc.gotbetter.room.data_access.repository;

import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

public interface RoomRepositoryQueryDSL {
    List<Room> findUserRooms(Long user_id);
    Room findRoomWithUserIdAndRoomId(Long user_id, Long room_id);
    void updatePlusTotalEntryFeeAndCurrentNum(Long room_id, Integer fee);
}
