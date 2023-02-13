package pcrc.gotbetter.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.service.UserReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserView {
    private final Long user_id;
    private final String auth_id;
    private final String username;
    private final String email;
    private final String profile;
    private final String access_token;
    private final String refresh_token;

    @Builder
    public UserView(UserReadUseCase.FindUserResult userResult) {
        this.user_id = userResult.getUser_id();
        this.auth_id = userResult.getAuth_id();
        this.username = userResult.getUsername();
        this.email = userResult.getEmail();
        this.profile = userResult.getProfile();
        this.access_token = userResult.getAccess_token();
        this.refresh_token = userResult.getRefresh_token();
    }
}
