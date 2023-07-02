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
        private final Integer maxUserNum;
        private final String startDate;
        private final Integer week;
        private final Integer currentWeek;
        private final Integer entryFee;
        private final Integer ruleId;
        private final String account;
        private final String description;
    }
}
