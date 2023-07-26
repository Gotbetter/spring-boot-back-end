package pcrc.gotbetter.setting.http_api;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GotBetterException extends RuntimeException {
	private final HttpStatus status;
	private final String type;

	public GotBetterException(MessageType messageType) {
		super(messageType.getMessage());
		this.status = messageType.getStatus(); //ex) 404
		this.type = messageType.name(); //ex) NOT_FOUND
	}
}
