package pcrc.gotbetter.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.service.UserReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserJoinView {
    private final String auth_id;
    private final String username;
    private final String email;

    @Builder
    public UserJoinView(UserReadUseCase.FindUserResult userResult) {
        this.auth_id = userResult.getAuth_id();
        this.username = userResult.getUsername();
        this.email = userResult.getEmail();
    }
}
