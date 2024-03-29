package pcrc.gotbetter.participant.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ParticipantJoinRequest {
	@NotBlank
	private String room_code;
}
