package pcrc.gotbetter.user.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserLoginRequest {

	@NotNull
	@NotBlank
	private String auth_id;
	@NotNull
	@NotBlank
	private String password;
}
