package pcrc.gotbetter.user.ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;
import pcrc.gotbetter.user.service.OAuthOperationUseCase;
import pcrc.gotbetter.user.ui.requestBody.OAuthRequest;
import pcrc.gotbetter.user.ui.view.OAuthView;

@Slf4j
@RestController
@RequestMapping(value = "/oauth")
public class OAuthController {
    private final OAuthOperationUseCase oAuthOperationUseCase;

    public OAuthController(OAuthOperationUseCase oAuthOperationUseCase) {
        this.oAuthOperationUseCase = oAuthOperationUseCase;
    }

    @PostMapping(value = "")
    public ResponseEntity<OAuthView> oAuthLogin(
        @RequestParam(name = "provider") String provider,
        @Valid @RequestBody OAuthRequest request
    ) {
        provider = provider.toUpperCase();

        if (!ProviderType.contains(provider)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        var command = OAuthOperationUseCase.OAuthLoginCommand.builder()
            .id(request.getId())
            .email(request.getEmail())
            .name(request.getName())
            .picture(request.getPicture())
            .build();
        TokenInfo tokenInfo = oAuthOperationUseCase.oAuthLogin(command);

        return ResponseEntity.created(null).body(OAuthView.builder().tokenInfo(tokenInfo).build());
    }
}
