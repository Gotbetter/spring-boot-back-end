package pcrc.gotbetter.push_notification.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.push_notification.service.FCMOperationUseCase;
import pcrc.gotbetter.push_notification.ui.requestBody.FCMRequest;

@Slf4j
@RestController
@RequestMapping(value = "/fcm")
public class FCMController {
	private final FCMOperationUseCase fcmOperationUseCase;

	@Autowired
	public FCMController(FCMOperationUseCase fcmOperationUseCase) {
		this.fcmOperationUseCase = fcmOperationUseCase;
	}

	@PostMapping(value = "")
	public void storeToken(@Valid @RequestBody FCMRequest request) {
		log.info("\"STORE FCM TOKEN\"");

		var command = FCMOperationUseCase.FCMStoreCommand.builder()
			.fcmToken(request.getFcm_token())
			.build();
		fcmOperationUseCase.storeToken(command);
		// return ResponseEntity.created(null).body();
	}
}