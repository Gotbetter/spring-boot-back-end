package pcrc.gotbetter.user.service;

import lombok.*;

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
