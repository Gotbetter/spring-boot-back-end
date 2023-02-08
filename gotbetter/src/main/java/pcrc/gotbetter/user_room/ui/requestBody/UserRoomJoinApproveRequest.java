package pcrc.gotbetter.user_room.ui.requestBody;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserRoomJoinApproveRequest {
    @NotNull
    private Long id;
    @NotNull
    private Long room_id;
}
