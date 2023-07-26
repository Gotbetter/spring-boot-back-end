package pcrc.gotbetter.room.data_access.repository;

import java.util.List;

import pcrc.gotbetter.room.data_access.entity.Room;

public interface RoomQueryRepository {

	List<Room> findListUnderWeek();

	List<Room> findListLastWeek();

	Integer findCurrentWeek(Long roomId);

	Boolean existByRoomCode(String roomCode);

}
