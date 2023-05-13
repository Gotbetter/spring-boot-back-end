package pcrc.gotbetter.user.login_method.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.user.data_access.entity.SocialAccount;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.SocialAccountRepository;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;
import pcrc.gotbetter.user.login_method.oauth.google.GoogleOAuthToken;
import pcrc.gotbetter.user.login_method.oauth.google.GoogleUser;

@Service
public class OAuthService {
    private final OauthInterface oauthInterface;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public OAuthService(OauthInterface oauthInterface, UserRepository userRepository,
                        SocialAccountRepository socialAccountRepository, JwtProvider jwtProvider) {
        this.oauthInterface = oauthInterface;
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtProvider = jwtProvider;
    }

    public String forCodeUrl() {
        return oauthInterface.getOAuthRedirectURL();
    }

    public TokenInfo oAuthLogin (String code) throws JsonProcessingException, ParseException {
        // get access token
        GoogleOAuthToken googleOAuthToken = oauthInterface.requestAccessToken(code);
        // get user info
        GoogleUser googleUser = oauthInterface.requestUserInfo(googleOAuthToken);

        Long userId;
        // 유저 정보 저장 (insert or select)
        if (socialAccountRepository.existsByProviderTypeAndProviderId(ProviderType.GOOGLE, googleUser.id)) {
            User findUser = userRepository.findByEmail(googleUser.email);
            userId = findUser.getUserId();
        } else {
            User findUser = userRepository.findByEmail(googleUser.email);
            if (findUser == null) {
                findUser = User.builder()
                        .username(googleUser.name)
                        .email(googleUser.email)
//                        .profile()
                        .build();
                userRepository.save(findUser);
            }
            SocialAccount saveSocialAccount = SocialAccount.builder()
                    .userId(findUser.getUserId())
                    .providerType(ProviderType.GOOGLE)
                    .providerId(googleUser.id)
                    .build();
            socialAccountRepository.save(saveSocialAccount);
            userId = findUser.getUserId();
        }
        // 토큰 만들기
        TokenInfo tokenInfo = jwtProvider.generateToken(userId.toString());
        userRepository.updateRefreshToken(userId, tokenInfo.getRefreshToken());
        return tokenInfo;
    }
}