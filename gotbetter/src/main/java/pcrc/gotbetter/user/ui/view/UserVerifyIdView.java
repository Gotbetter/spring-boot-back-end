package pcrc.gotbetter.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.service.UserReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVerifyIdView {
    private final String auth_id;

    @Builder
    public UserVerifyIdView(UserReadUseCase.FindUserResult userResult) {
        this.auth_id = userResult.getAuth_id();
    }
}
