package pcrc.gotbetter.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.login_method.login_type.RoleType;
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
	private final RoleType role_type;
	private final String access_token;
	private final String refresh_token;
	private final String created_date;
	private final String updated_date;

	@Builder
	public UserView(UserReadUseCase.FindUserResult userResult) {
		this.user_id = userResult.getUserId();
		this.auth_id = userResult.getAuthId();
		this.username = userResult.getUsername();
		this.email = userResult.getEmail();
		this.profile = userResult.getProfile();
		this.role_type = userResult.getRoleType();
		this.access_token = userResult.getAccessToken();
		this.refresh_token = userResult.getRefreshToken();
		this.created_date = userResult.getCreatedDate();
		this.updated_date = userResult.getUpdatedDate();
	}
}
