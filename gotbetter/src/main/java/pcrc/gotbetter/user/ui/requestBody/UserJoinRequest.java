package pcrc.gotbetter.user.ui.requestBody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserJoinRequest {

	@NotBlank
	private String auth_id;
	@NotBlank
	private String password;
	@NotBlank
	private String username;
	@NotBlank
	@Email
	private String email;

}