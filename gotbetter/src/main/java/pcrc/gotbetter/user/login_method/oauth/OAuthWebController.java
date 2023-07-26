package pcrc.gotbetter.user.login_method.oauth;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletResponse;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;

@Controller
public class OAuthWebController {
	private final OAuthWebService oAuthWebService;

	public OAuthWebController(OAuthWebService oAuthWebService) {
		this.oAuthWebService = oAuthWebService;
	}

	@GetMapping(value = "/oauth")
	public void socialLoginRequest(HttpServletResponse response) throws IOException {
		String requestURL = oAuthWebService.forCodeUrl();
		response.sendRedirect(requestURL);
	}

	@GetMapping(value = "/oauth/redirect")
	public ResponseEntity<TokenInfo> callback(@RequestParam(name = "code") String code) throws
		JsonProcessingException,
		ParseException {
		TokenInfo tokenInfo = oAuthWebService.oAuthLogin(code);

		return ResponseEntity.ok(tokenInfo);
		// return ResponseEntity.status(HttpStatus.FOUND)
		// .location(ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8081/rules").build().toUri())
		// .build();
	}
}