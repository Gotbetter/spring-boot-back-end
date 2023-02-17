package pcrc.gotbetter.user.data_access.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String authId;
    private String usernameNick;
    private String email;
    private String profile;

    @QueryProjection
    public UserDto(Long userId, String authId, String usernameNick,
                   String email, String profile) {
        this.userId = userId;
        this.authId = authId;
        this.usernameNick = usernameNick;
        this.email = email;
        this.profile = profile;
    }
}