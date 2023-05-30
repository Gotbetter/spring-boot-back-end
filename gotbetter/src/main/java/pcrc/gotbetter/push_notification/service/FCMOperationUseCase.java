package pcrc.gotbetter.push_notification.service;

import java.io.IOException;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface FCMOperationUseCase {
	void storeToken(FCMStoreCommand command);
	void sendNotifications(String targetToken, String title, String body) throws IOException;

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class FCMStoreCommand {
		private final String fcmToken;
	}
}
