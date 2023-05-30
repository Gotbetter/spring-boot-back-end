package pcrc.gotbetter.push_notification.ui.requestBody;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class FCMRequest {
	@NotNull
	private String fcm_token;
}
