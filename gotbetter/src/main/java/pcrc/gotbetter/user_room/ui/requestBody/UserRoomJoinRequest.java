package pcrc.gotbetter.user_room.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserRoomJoinRequest {
    @NotNull
    @NotBlank
    private String room_code;
}
