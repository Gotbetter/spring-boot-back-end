package pcrc.gotbetter.user.login_method.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;

import java.io.IOException;

@Controller
public class OAuthController {
    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping(value = "/oauth")
    public void socialLoginRequest(HttpServletResponse response) throws IOException {
        String requestURL = oAuthService.forCodeUrl();
        response.sendRedirect(requestURL);
    }

    @GetMapping(value = "/oauth/redirect")
    public ResponseEntity<TokenInfo> callback(@RequestParam(name = "code") String code) throws JsonProcessingException, ParseException {
        TokenInfo tokenInfo = oAuthService.oAuthLogin(code);

        return ResponseEntity.ok(tokenInfo);
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8081/rules").build().toUri())
//                .build();
    }
}