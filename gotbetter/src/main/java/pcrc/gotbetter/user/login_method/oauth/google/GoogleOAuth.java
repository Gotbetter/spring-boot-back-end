package pcrc.gotbetter.user.login_method.oauth.google;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pcrc.gotbetter.user.login_method.oauth.OauthInterface;

@Component
public class GoogleOAuth implements OauthInterface {
	@Value("${spring.security.oauth2.client.registration.google.url}")
	private String GOOGLE_LOGIN_URL;

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String GOOGLE_CLIENT_ID;

	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String GOOGLE_CLIENT_SECRET;

	@Value("${spring.security.oauth2.client.registration.google.redirect-url}")
	private String GOOGLE_CALLBACK_URL;

	@Value("${spring.security.oauth2.client.registration.google.scope}")
	private String GOOGLE_DATA_ACCESS_SCOPE;

	private final RestTemplateBuilder restTemplateBuilder;

	private final ObjectMapper objectMapper;

	public GoogleOAuth(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
		this.restTemplateBuilder = restTemplateBuilder;
		this.objectMapper = objectMapper;
	}

	@Override
	public String getOAuthRedirectURL() {
		Map<String, Object> params = new HashMap<>();

		params.put("scope", GOOGLE_DATA_ACCESS_SCOPE);
		params.put("response_type", "code");
		params.put("client_id", GOOGLE_CLIENT_ID);
		params.put("redirect_uri", GOOGLE_CALLBACK_URL);

		String queryParameter = params.entrySet().stream()
			.map(x -> x.getKey() + "=" + x.getValue())
			.collect(Collectors.joining("&"));
		return GOOGLE_LOGIN_URL + "?" + queryParameter;
	}

	@Override
	public GoogleOAuthToken requestAccessToken(String code) throws ParseException {
		String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> params = new HashMap<>();

		params.put("code", code);
		params.put("client_id", GOOGLE_CLIENT_ID);
		params.put("client_secret", GOOGLE_CLIENT_SECRET);
		params.put("redirect_uri", GOOGLE_CALLBACK_URL);
		params.put("grant_type", "authorization_code");

		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, params,
			String.class);
		String body = stringResponseEntity.getBody();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(body);
		JSONObject jsonObject = (JSONObject)obj;

		return GoogleOAuthToken.builder()
			.access_token((String)jsonObject.get("access_token"))
			.expires_in((Long)jsonObject.get("expires_in"))
			.scope((String)jsonObject.get("scope"))
			.token_type((String)jsonObject.get("token_type"))
			.id_token((String)jsonObject.get("id_token"))
			.build();
	}

	@Override
	public GoogleUser requestUserInfo(GoogleOAuthToken googleOAuthToken) throws ParseException {
		String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		headers.add("Authorization", "Bearer " + googleOAuthToken.getAccess_token());

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> exchange = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request,
			String.class);
		String body = exchange.getBody();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(body);
		JSONObject jsonObject = (JSONObject)obj;

		return GoogleUser.builder()
			.id((String)jsonObject.get("id"))
			.email((String)jsonObject.get("email"))
			.verifiedEmail((Boolean)jsonObject.get("verifiedEmail"))
			.name((String)jsonObject.get("name"))
			.givenName((String)jsonObject.get("givenName"))
			.familyName((String)jsonObject.get("familyName"))
			.picture((String)jsonObject.get("picture"))
			.locale((String)jsonObject.get("locale"))
			.build();
	}
}
