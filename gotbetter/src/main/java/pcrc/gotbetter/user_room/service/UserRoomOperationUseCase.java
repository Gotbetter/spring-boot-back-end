package pcrc.gotbetter.user_room.service;

import pcrc.gotbetter.room.service.RoomReadUseCase;

public interface UserRoomOperationUseCase {
    RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code);
}
