package pcrc.gotbetter.user.service;

import lombok.*;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.data_access.entity.User;

import java.io.IOException;

public interface UserReadUseCase {

    FindUserResult loginUser(UserFindQuery query);
    FindUserResult verifyId(String authId);
    FindUserResult getUserInfo() throws IOException;

    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    @Builder
    class UserFindQuery {
        private final String authId;
        private final String password;
    }

    @Getter
    @ToString
    @Builder
    class FindUserResult {
        private final Long userId;
        private final String authId;
        private final String username;
        private final String email;
        private final String profile;
        private final String accessToken;
        private final String refreshToken;

        public static FindUserResult findByUser(User user, UserSet userSet, TokenInfo tokenInfo) {
            return FindUserResult.builder()
                    .userId(user.getUserId())
                    .authId(userSet.getAuthId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .profile(user.getProfile())
                    .accessToken(tokenInfo.getAccessToken())
                    .refreshToken(tokenInfo.getRefreshToken())
                    .build();
        }
    }

}
