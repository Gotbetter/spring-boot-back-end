package pcrc.gotbetter.user.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;

public interface OAuthOperationUseCase {
    TokenInfo oAuthLogin(OAuthLoginCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class OAuthLoginCommand {
        private final String id;
        private final String email;
        private final String name;
        private final String picture;
    }
}
