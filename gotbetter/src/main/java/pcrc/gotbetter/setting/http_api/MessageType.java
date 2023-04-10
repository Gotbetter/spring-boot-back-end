package pcrc.gotbetter.setting.http_api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {
    BAD_REQUEST ("Check API request URL protocol, parameter, etc. for errors", HttpStatus.BAD_REQUEST),
    NOT_FOUND ("No data was found for the server. Please refer to parameter description.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR ("An error occurred inside the server.", HttpStatus.INTERNAL_SERVER_ERROR),
    CONFLICT ("Already exists data.", HttpStatus.CONFLICT),
    CONFLICT_JOIN ("Already in the room.", HttpStatus.CONFLICT),
    CONFLICT_MAX("Already full.", HttpStatus.CONFLICT),
    FORBIDDEN ("Do not have an authorization.", HttpStatus.FORBIDDEN),
    FORBIDDEN_DATE("It's not the day.", HttpStatus.FORBIDDEN),
    ExpiredJwtException ("Access Token is expired.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED ("You can use it after login.", HttpStatus.UNAUTHORIZED),
    UsernameOrPasswordNotFound ("Not existed user.", HttpStatus.BAD_REQUEST),
    ReLogin ("Access Token and Refresh Token are expired.", HttpStatus.UNAUTHORIZED),
    MalformedJwtException ("Access token is malformed.", HttpStatus.BAD_REQUEST),
    IllegalArgumentJwtException("Access token is illegal.", HttpStatus.BAD_REQUEST),
    NotAcceptable("If the request is normal, but the request cannot be received by the server.", HttpStatus.NOT_ACCEPTABLE)
    ;

    private final String message;
    private final HttpStatus status;

    MessageType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

}
