package pcrc.gotbetter.user.service;

import lombok.*;
import pcrc.gotbetter.setting.security.JWT.TokenInfo;
import pcrc.gotbetter.user.data_access.domain.User;

import java.io.IOException;

public interface UserReadUseCase {

    FindUserResult loginUser(UserFindQuery query) throws IOException;
    FindUserResult verifyId(String auth_id);

    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    @Builder
    class UserFindQuery {
        private final String auth_id;
        private final String password;
    }

    @Getter
    @ToString
    @Builder
    class FindUserResult {
        private final Long id;
        private final String auth_id;
        private final String username;
        private final String email;
        private final String profile;
        private final String access_token;
        private final String refresh_token;

        public static FindUserResult findByUser(User user, TokenInfo tokenInfo) {
            return FindUserResult.builder()
                    .id(user.getId())
                    .auth_id(user.getAuthId())
                    .username(user.getUsernameNick())
                    .email(user.getEmail())
                    .profile(user.getProfile())
                    .access_token(tokenInfo.getAccessToken())
                    .refresh_token(tokenInfo.getRefreshToken())
                    .build();
        }
    }

}
