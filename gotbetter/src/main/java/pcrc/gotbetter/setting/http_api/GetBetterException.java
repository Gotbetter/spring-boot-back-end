package pcrc.gotbetter.setting.http_api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GetBetterException extends RuntimeException {
    private final HttpStatus status;
    private final String type;

    public GetBetterException(MessageType messageType) {
        super(messageType.getMessage());
        this.status = messageType.getStatus(); //ex) 404
        this.type = messageType.name(); //ex) NOT_FOUND
    }
}
