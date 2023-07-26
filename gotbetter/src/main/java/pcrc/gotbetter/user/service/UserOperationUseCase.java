package pcrc.gotbetter.user.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserOperationUseCase {

    UserReadUseCase.FindUserResult createUser(UserCreateCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class UserCreateCommand {
        private final String authId;
        private final String password;
        private final String username;
        private final String email;
    }

}
