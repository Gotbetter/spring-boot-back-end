package pcrc.gotbetter.push_notification;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FCMInitializer {
	@Value("${fcm.certification}")
	private String CREDENTIAL;

	@PostConstruct
	public void initialize() {
		ClassPathResource resource = new ClassPathResource(CREDENTIAL);

		try (InputStream stream = resource.getInputStream()) {
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(stream))
				.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				log.info("FirebaseApp initialization complete");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// throw new ApiException(ExceptionEnum.INTERNAL_SERVER_ERROR);
		}

	}
}