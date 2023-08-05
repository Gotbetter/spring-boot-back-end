package pcrc.gotbetter.user.ui.requestBody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OAuthRequest {
	@NotBlank
	private String id;
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String name;
	@NotBlank
	private String picture;
}
