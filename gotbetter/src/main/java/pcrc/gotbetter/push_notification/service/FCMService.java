package pcrc.gotbetter.push_notification.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pcrc.gotbetter.push_notification.FCMMessageDto;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

@Slf4j
@Service
public class FCMService implements FCMOperationUseCase {
	@Value("${fcm.certification}")
	private String CREDENTIAL;
	@Value("${fcm.api.url}")
	private String API_URL;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;

	@Autowired
	public FCMService(ObjectMapper objectMapper, UserRepository userRepository) {
		this.objectMapper = objectMapper;
		this.userRepository = userRepository;
	}

	@Override
	public void storeToken(FCMStoreCommand command) {
		Long currentUserId = getCurrentUserId();
		User findUser = userRepository.findByUserId(currentUserId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		findUser.updateFcmToken(command.getFcmToken());
		userRepository.save(findUser);
	}

	@Override
	public void sendNotifications(String targetToken, String title, String body) throws IOException {
		String message = makeMessage(targetToken, title, body);
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
		Request request = new Request.Builder()
			.url(API_URL)
			.post(requestBody)
			.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
			.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
			.build();
		Response response = client.newCall(request).execute();

		assert response.body() != null;
		log.info(response.body().string());
	}

	private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
		// FCMMessage
		FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
			.message(FCMMessageDto.Message.builder()
				.token(targetToken)
				.notification(FCMMessageDto.Notification.builder()
					.title(title)
					.body(body)
					.build()
				)
				.build()
			)
			.validateOnly(false)
			.build();
		return objectMapper.writeValueAsString(fcmMessageDto);
	}

	private String getAccessToken() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(CREDENTIAL).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
		googleCredentials.refreshIfExpired();

		return googleCredentials.getAccessToken().getTokenValue();
	}
}
