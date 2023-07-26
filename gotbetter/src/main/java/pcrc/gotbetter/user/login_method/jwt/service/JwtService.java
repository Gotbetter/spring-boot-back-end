package pcrc.gotbetter.user.login_method.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

import java.util.Date;

@Service
public class JwtService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Autowired
    public JwtService(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public TokenInfo reissueNewAccessToken(HttpServletRequest request) {

        String refreshToken = jwtProvider.extractToken(request);

        if (!jwtProvider.validateJwtToken(request, refreshToken)) {
            throw new GotBetterException(MessageType.ReLogin);
        }

        User user = validateRefreshToken(refreshToken);
        long diffDays = compareDate(jwtProvider.parseClaims(refreshToken).getExpiration());
        TokenInfo tokenInfo = jwtProvider.generateToken(user.getUserId().toString());
        if (diffDays < 30) {
            user.updateRefreshToken(tokenInfo.getRefreshToken());
            user.updateById(user.getUserId().toString());
            userRepository.save(user);
        } else {
            tokenInfo.setRefreshToken(refreshToken);
        }
        return tokenInfo;
    }

    /**
     * validate section
     */
    private User validateRefreshToken(String refreshToken) {
        String userId = (String) jwtProvider.parseClaims(refreshToken).get("id");
        User user = userRepository.findByUserId(Long.valueOf(userId))
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.ReLogin);
                });
        if (!user.getRefreshToken().equals(refreshToken)) {
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
