package pcrc.gotbetter.room.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

public interface RoomOperationUseCase {

    RoomReadUseCase.FindRoomResult createRoom(RoomCreateCommand command);
    RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class RoomCreateCommand {
        private final String title;
        private final Integer max_user_num;
        private final Date start_date;
        private final Date target_date;
        private final Integer entry_fee;
        private final Integer rule_id;
        private final String account;
    }
}
