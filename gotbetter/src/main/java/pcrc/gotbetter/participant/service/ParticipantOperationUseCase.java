package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;

public interface ParticipantOperationUseCase {
    RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code);
    ParticipantReadUseCase.FindParticipantResult approveJoinRoom(UserRoomAcceptedUpdateCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserRoomAcceptedUpdateCommand {
        private final Long user_id;
        private final Long room_id;
    }
}
