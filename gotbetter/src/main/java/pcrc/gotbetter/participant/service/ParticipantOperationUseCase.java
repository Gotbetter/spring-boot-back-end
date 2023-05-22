package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;

public interface ParticipantOperationUseCase {
    RoomReadUseCase.FindRoomResult requestJoinRoom(String roomCode);
    ParticipantReadUseCase.FindParticipantResult approveJoinRoom(UserRoomAcceptedUpdateCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserRoomAcceptedUpdateCommand {
        private final Long userId;
        private final Long roomId;
    }
}
