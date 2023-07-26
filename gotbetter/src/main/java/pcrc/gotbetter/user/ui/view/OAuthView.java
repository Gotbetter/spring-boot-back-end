package pcrc.gotbetter.user.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthView {
	private final String access_token;
	private final String refresh_token;

	@Builder
	public OAuthView(TokenInfo tokenInfo) {
		this.access_token = tokenInfo.getAccessToken();
		this.refresh_token = tokenInfo.getRefreshToken();
	}

}
