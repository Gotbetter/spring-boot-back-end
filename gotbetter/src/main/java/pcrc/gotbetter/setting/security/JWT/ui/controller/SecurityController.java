package pcrc.gotbetter.setting.security.JWT.ui.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.setting.security.JWT.JwtProvider;
import pcrc.gotbetter.setting.security.JWT.service.SecurityService;
import pcrc.gotbetter.setting.security.JWT.TokenInfo;
import pcrc.gotbetter.setting.security.JWT.ui.view.AccessTokenView;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class SecurityController {
    private final JwtProvider jwtProvider;
    private final SecurityService securityService;

    @Autowired
    public SecurityController(JwtProvider jwtProvider, SecurityService securityService) {
        this.jwtProvider = jwtProvider;
        this.securityService = securityService;
    }

    @PostMapping(value = "/reissue")
    public ResponseEntity<AccessTokenView> reissue(HttpServletRequest request) {
        log.info("REISSUE");

        String refreshToken = jwtProvider.resolveToken(request);

        if (!jwtProvider.validateJwtToken(request, refreshToken) ||
        !securityService.validateRefreshToken(refreshToken)) {
            throw new GotBetterException(MessageType.ReLogin);
        }

        TokenInfo tokenInfo = securityService.reissueNewAccessToken(refreshToken);

        return ResponseEntity.ok(AccessTokenView.builder().accessToken(tokenInfo.getAccessToken()).build());
    }
}
