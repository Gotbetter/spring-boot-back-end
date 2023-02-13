package pcrc.gotbetter.participant.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ParticipantJoinRequest {
    @NotNull
    @NotBlank
    private String room_code;
}
