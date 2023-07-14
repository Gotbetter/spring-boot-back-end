package pcrc.gotbetter.room.data_access.repository;

import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

public interface RoomRepositoryQueryDSL {

    List<Room> findListUnderWeek();

    List<Room> findListLastWeek();

    Integer findCurrentWeek(Long roomId);

    Boolean existByRoomCode(String roomCode);

}
