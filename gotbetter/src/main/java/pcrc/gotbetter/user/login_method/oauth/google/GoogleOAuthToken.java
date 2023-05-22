package pcrc.gotbetter.user.login_method.oauth.google;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleOAuthToken {
    private String access_token;
    private Long expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
