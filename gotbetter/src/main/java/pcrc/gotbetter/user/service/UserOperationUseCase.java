package pcrc.gotbetter.user.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface UserOperationUseCase {

	UserReadUseCase.FindUserResult createUser(UserCreateCommand command);

	void changeAuthentication(UserAdminUpdateCommand command);

	void deleteUser(Long userId);

	void updateUserInfo(UserUpdateCommand command);

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

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class UserAdminUpdateCommand {
		private final Long userId;
		private final Boolean approve;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class UserUpdateCommand {
		private final Long userId;
		private final String username;
	}

}
