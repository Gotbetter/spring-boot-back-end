package pcrc.gotbetter.participant.ui.requestBody;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ParticipantJoinApproveRequest {
	@NotNull
	private Long user_id;
	@NotNull
	private Long room_id;
}
