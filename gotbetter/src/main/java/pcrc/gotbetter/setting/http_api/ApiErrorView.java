package pcrc.gotbetter.setting.http_api;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiErrorView {
	private final List<Error> errors;

	public ApiErrorView(List<MessageType> messageTypes) {
		this.errors = messageTypes.stream().map(Error::errorWithMessageType).collect(Collectors.toList());
	}

	public ApiErrorView(GotBetterException exception) {
		this.errors = Collections.singletonList(Error.errorWithException(exception));
	}

	public ApiErrorView(MessageType messageType, String message) {
		this.errors = Collections.singletonList(Error.errorWithMessageTypeAndMessage(messageType, message));
	}

	@Getter
	@ToString
	public static class Error {
		private final String errorType;
		private final String errorMessage;

		public static Error errorWithMessageType(MessageType messageType) {
			return new Error(messageType.name(), messageType.getMessage());
		}

		public static Error errorWithMessageTypeAndMessage(MessageType messageType, String message) {
			return new Error(messageType.name(), message);
		}

		public static Error errorWithException(GotBetterException gotBetterException) {
			return new Error(gotBetterException);
		}

		private Error(String errorType, String errorMessage) {
			this.errorType = errorType;
			this.errorMessage = errorMessage;
		}

		private Error(GotBetterException gotBetterException) {
			this.errorType =
				ObjectUtils.isEmpty(gotBetterException.getType()) ? gotBetterException.getStatus().getReasonPhrase() :
					gotBetterException.getType();
			this.errorMessage = gotBetterException.getMessage();
		}
	}
}
