package pcrc.gotbetter.user.service;

import lombok.*;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.data_access.entity.User;

import java.io.IOException;

public interface UserReadUseCase {

    FindUserResult loginUser(UserFindQuery query);
    FindUserResult verifyId(String auth_id);
    FindUserResult getUserInfo() throws IOException;

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
        private final Long user_id;
        private final String auth_id;
        private final String username;
        private final String email;
        private final String profile;
        private final String access_token;
        private final String refresh_token;

        public static FindUserResult findByUser(User user, UserSet userSet, TokenInfo tokenInfo) {
            return FindUserResult.builder()
                    .user_id(user.getUserId())
                    .auth_id(userSet.getAuthId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .profile(user.getProfile())
                    .access_token(tokenInfo.getAccessToken())
                    .refresh_token(tokenInfo.getRefreshToken())
                    .build();
        }
    }

}
