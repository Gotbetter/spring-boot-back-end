package pcrc.gotbetter.user_room.service;

import pcrc.gotbetter.user.service.UserReadUseCase;

import java.util.List;

public interface UserRoomReadUseCase {
    List<UserReadUseCase.FindUserResult> getMemberListInARoom(Long room_id, Boolean accepted);
}
