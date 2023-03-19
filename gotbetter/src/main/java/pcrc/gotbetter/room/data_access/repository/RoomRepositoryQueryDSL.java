package pcrc.gotbetter.room.data_access.repository;

import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

public interface RoomRepositoryQueryDSL {
    // insert, update, delete
    void updatePlusTotalEntryFeeAndCurrentNum(Long room_id, Integer fee);
    void updateCurrentWeek(Long room_id, Integer changeWeek);

    // select
    List<Room> findListUnderWeek();
    List<Room> findListLastWeek();
    Integer findCurrentWeek(Long room_id);
    Boolean existByRoomCode(String room_code);
}
