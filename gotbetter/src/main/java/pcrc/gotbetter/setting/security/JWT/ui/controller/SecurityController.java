package pcrc.gotbetter.setting.security.JWT.ui.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pcrc.gotbetter.setting.security.JWT.service.SecurityService;
import pcrc.gotbetter.setting.security.JWT.ui.view.AccessTokenView;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class SecurityController {
    private final SecurityService securityService;

    @Autowired
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping(value = "/reissue")
    public ResponseEntity<AccessTokenView> reissue(HttpServletRequest request) {
        log.info("REISSUE");

        String accessToken = securityService.reissueNewAccessToken(request);

        return ResponseEntity.ok(AccessTokenView.builder().accessToken(accessToken).build());
    }
}