package pcrc.gotbetter.room.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface RoomOperationUseCase {

    RoomReadUseCase.FindRoomResult createRoom(RoomCreateCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class RoomCreateCommand {
        private final String title;
        private final Integer max_user_num;
        private final String start_date;
        private final Integer week;
        private final Integer current_week;
        private final Integer entry_fee;
        private final Integer rule_id;
        private final String account;
    }
}
