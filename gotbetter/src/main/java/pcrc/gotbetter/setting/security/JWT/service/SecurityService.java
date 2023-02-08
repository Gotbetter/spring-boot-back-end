package pcrc.gotbetter.setting.security.JWT.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.setting.security.JWT.JwtProvider;
import pcrc.gotbetter.setting.security.JWT.TokenInfo;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

import java.util.Date;

@Service
public class SecurityService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Autowired
    public SecurityService(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public TokenInfo reissueNewAccessToken(HttpServletRequest request) {

        String refreshToken = jwtProvider.resolveToken(request);

        if (!jwtProvider.validateJwtToken(request, refreshToken)) {
            throw new GotBetterException(MessageType.ReLogin);
        }

        User user = validateRefreshToken(refreshToken);
        long diffDays = compareDate(jwtProvider.parseClaims(refreshToken).getExpiration());
        TokenInfo tokenInfo = jwtProvider.generateToken(user.getAuthId());
        if (diffDays < 30) {
            userRepository.updateRefreshToken(user.getAuthId(), tokenInfo.getRefreshToken());
        } else {
            tokenInfo.setRefreshToken(refreshToken);
        }
        return tokenInfo;
    }

    /**
     * validate section
     */
    private User validateRefreshToken(String refreshToken) {
        String auth_id = (String) jwtProvider.parseClaims(refreshToken).get("id");
        User user = userRepository.findByAuthId(auth_id)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.ReLogin);
                });
        if (!user.getRefresh_token().equals(refreshToken)) {
            throw new GotBetterException(MessageType.ReLogin);
        }
        return user;
    }

    private Long compareDate(Date expired) {
        Date today = new Date();
        long diffSec = (expired.getTime() - today.getTime()) / 1000;
        return diffSec / (24 * 60 * 60);
    }

}
