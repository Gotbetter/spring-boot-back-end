package pcrc.gotbetter.user.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

public interface UserReadUseCase {

	FindUserResult loginUser(UserFindQuery query);

	FindUserResult verifyId(String authId);

	FindUserResult getUserInfo() throws IOException;

	List<FindUserResult> getAllUserInfo() throws IOException;

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class UserFindQuery {
		private final String authId;
		private final String password;
		private final Boolean isAdmin;
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
		private final RoleType roleType;
		private final String accessToken;
		private final String refreshToken;
		private final LocalDate createdDate;

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

		public static FindUserResult findByUsers(User user, UserSet userSet, String profile) {
			return FindUserResult.builder()
				.userId(user.getUserId())
				.authId(userSet.getAuthId())
				.username(user.getUsername())
				.email(user.getEmail())
				.profile(profile)
				.roleType(user.getRoleType())
				.createdDate(user.getCreatedDate().toLocalDate())
				.build();
		}
	}

}
