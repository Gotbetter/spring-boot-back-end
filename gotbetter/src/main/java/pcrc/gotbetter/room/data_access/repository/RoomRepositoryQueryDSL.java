package pcrc.gotbetter.room.data_access.repository;

import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

public interface RoomRepositoryQueryDSL {
    // insert, update, delete
    void updatePlusTotalEntryFeeAndCurrentNum(Long room_id, Integer fee);
    void updateCurrentWeek(Long room_id, Integer plusWeek);

    // select
    List<Room> findListUnderWeek();
    Boolean existByRoomCode(String room_code);
}
