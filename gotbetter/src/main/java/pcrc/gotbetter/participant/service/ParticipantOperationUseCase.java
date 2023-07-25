package pcrc.gotbetter.participant.service;

import java.io.IOException;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;

public interface ParticipantOperationUseCase {
    RoomReadUseCase.FindRoomResult requestJoinRoom(String roomCode);
    ParticipantReadUseCase.FindParticipantResult approveJoinRoom(UserRoomAcceptedCommand command) throws IOException;
    void rejectJoinRoom(UserRoomAcceptedCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserRoomAcceptedCommand {
        private final Long userId;
        private final Long roomId;
    }
}
