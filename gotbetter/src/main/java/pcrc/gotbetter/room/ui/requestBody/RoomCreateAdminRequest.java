package pcrc.gotbetter.room.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RoomCreateAdminRequest {
	@NotBlank
	private String title;
	@NotNull
	private Integer max_user_num;
	@NotBlank
	private String start_date;
	@NotNull
	private Integer week;
	@NotNull
	private Integer entry_fee;
	@NotBlank
	private String account;
	@NotBlank
	private String room_category_code;
	@NotBlank
	private String rule_code;
	@NotNull
	private Long user_id;
	@NotNull
	private String description;
}
