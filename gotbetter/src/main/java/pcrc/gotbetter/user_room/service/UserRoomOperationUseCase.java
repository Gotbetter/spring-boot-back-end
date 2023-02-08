package pcrc.gotbetter.user_room.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.user.service.UserReadUseCase;

public interface UserRoomOperationUseCase {
    RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code);
    UserReadUseCase.FindUserResult approveJoinRoom(UserRoomAcceptedUpdateCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserRoomAcceptedUpdateCommand {
        private final Long id;
        private final Long room_id;
    }
}
