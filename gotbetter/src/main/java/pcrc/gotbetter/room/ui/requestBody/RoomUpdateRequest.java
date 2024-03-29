package pcrc.gotbetter.room.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RoomUpdateRequest {
	@NotBlank
	private String title;
	@NotBlank
	private String account;
	@NotNull
	private Integer max_user_num;
	@NotBlank
	private String room_category_code;
	@NotBlank
	private String rule_code;
	@NotNull
	private Long leader_id;

	// @NotBlank
	// private String room_code;
	// @NotNull
	// private Integer entry_fee;
	// @NotNull
	// private Integer week;
	// private Integer total_entry_fee;
	// private Integer current_week;
	// private String start_date;
	// private Integer current_user_num;

}