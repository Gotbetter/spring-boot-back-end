package pcrc.gotbetter.room.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RoomCreateRequest {
    @NotNull @NotBlank
    private String title;
    @NotNull
    private Integer max_user_num;
    @NotNull @NotBlank
    private String start_date;
    @NotNull
    private Integer week;
    @NotNull
    private Integer current_week;
    @NotNull
    private Integer entry_fee;
    @NotNull @NotBlank
    private String rule_code;
    @NotNull @NotBlank
    private String account;
    private String room_category_code;
    private String description;
}
