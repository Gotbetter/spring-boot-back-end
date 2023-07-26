package pcrc.gotbetter.user.login_method.jwt.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.jwt.service.JwtService;
import pcrc.gotbetter.user.login_method.jwt.ui.view.TokenView;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class SecurityController {
	private final JwtService jwtService;

	@Autowired
	public SecurityController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@PostMapping(value = "/reissue")
	public ResponseEntity<TokenView> reissue(HttpServletRequest request) {

		log.info("\"REISSUE\"");

		TokenInfo tokenInfo = jwtService.reissueNewAccessToken(request);

		return ResponseEntity.ok(TokenView.builder().tokenInfo(tokenInfo).build());
	}
}