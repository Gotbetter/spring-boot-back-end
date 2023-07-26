package pcrc.gotbetter.user.login_method.oauth.google;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class GoogleUser {
	public String id;
	public String email;
	public Boolean verifiedEmail;
	public String name;
	public String givenName;
	public String familyName;
	public String picture;
	public String locale;
}
