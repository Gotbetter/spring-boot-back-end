package pcrc.gotbetter.room.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
public class RoomCreateRequest {
    @NotNull @NotBlank
    private String title;
    @NotNull
    private Integer max_user_num;
    @NotNull
    private Date start_date;
    @NotNull
    private Date target_date;
    @NotNull
    private Integer entry_fee;
    @NotNull
    private Integer rule_id;
    @NotNull @NotBlank
    private String account;
}
