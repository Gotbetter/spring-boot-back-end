package pcrc.gotbetter.setting.security.JWT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.setting.security.JWT.JwtProvider;
import pcrc.gotbetter.setting.security.JWT.TokenInfo;
import pcrc.gotbetter.user.data_access.domain.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

@Service
public class SecurityService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Autowired
    public SecurityService(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public boolean validateRefreshToken(String requestToken) {

        String auth_id = (String) jwtProvider.parseClaims(requestToken).get("id");

        User user = userRepository.findByAuthId(auth_id)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.ReLogin);
                });

        return user.getRefresh_token().equals(requestToken);
    }

    public TokenInfo reissueNewAccessToken(String refreshToken) {

        String auth_id = (String) jwtProvider.parseClaims(refreshToken).get("id");

        return jwtProvider.generateToken(auth_id);
    }
}
