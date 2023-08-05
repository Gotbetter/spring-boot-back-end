package pcrc.gotbetter.user.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserVerifyIdRequest {

	@NotBlank
	private String auth_id;
}
