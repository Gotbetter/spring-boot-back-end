package pcrc.gotbetter.user.login_method.oauth;

import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import pcrc.gotbetter.user.login_method.oauth.google.GoogleOAuthToken;
import pcrc.gotbetter.user.login_method.oauth.google.GoogleUser;

public interface OauthInterface {
	String getOAuthRedirectURL();

	GoogleOAuthToken requestAccessToken(String code) throws JsonProcessingException, ParseException;

	GoogleUser requestUserInfo(GoogleOAuthToken googleOAuthToken) throws ParseException;
}
