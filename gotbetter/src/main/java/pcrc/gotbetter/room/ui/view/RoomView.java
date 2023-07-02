package pcrc.gotbetter.room.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomView {
    private final Long room_id;
    private final String title;
    private final Integer max_user_num;
    private final Integer current_user_num;
    private final String start_date;
    private final Integer week;
    private final Integer current_week;
    private final Integer entry_fee;
    private final String room_code;
    private final String account;
    private final String description;
    private final Integer total_entry_fee;
    private final Integer rule_id;
    private final Long participant_id;

    @Builder
    public RoomView(RoomReadUseCase.FindRoomResult roomResult) {
        this.room_id = roomResult.getRoomId();
        this.title = roomResult.getTitle();
        this.max_user_num = roomResult.getMaxUserNum();
        this.current_user_num = roomResult.getCurrentUserNum();
        this.start_date = roomResult.getStartDate();
        this.week = roomResult.getWeek();
        this.current_week = roomResult.getCurrentWeek();
        this.entry_fee = roomResult.getEntryFee();
        this.room_code = roomResult.getRoomCode();
        this.account = roomResult.getAccount();
        this.description = roomResult.getDescription();
        this.total_entry_fee = roomResult.getTotalEntryFee();
        this.rule_id = roomResult.getRuleId();
        this.participant_id = roomResult.getParticipantId();
    }
}
