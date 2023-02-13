package pcrc.gotbetter.room.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;

import java.time.LocalDate;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomView {
    private final Long room_id;
    private final String title;
    private final Integer max_user_num;
    private final Integer current_user_num;
    private final LocalDate start_date;
    private final Integer week;
    private final Integer current_week;
    private final Integer entry_fee;
    private final String room_code;
    private final String account;
    private final Integer total_entry_fee;
    private final Integer rule_id;
    private final Long participant_id;

    @Builder
    public RoomView(RoomReadUseCase.FindRoomResult roomResult) {
        this.room_id = roomResult.getRoom_id();
        this.title = roomResult.getTitle();
        this.max_user_num = roomResult.getMax_user_num();
        this.current_user_num = roomResult.getCurrent_user_num();
        this.start_date = roomResult.getStart_date();
        this.week = roomResult.getWeek();
        this.current_week = roomResult.getCurrent_week();
        this.entry_fee = roomResult.getEntry_fee();
        this.room_code = roomResult.getRoom_code();
        this.account = roomResult.getAccount();
        this.total_entry_fee = roomResult.getTotal_entry_fee();
        this.rule_id = roomResult.getRule_id();
        this.participant_id = roomResult.getParticipant_id();
    }
}
