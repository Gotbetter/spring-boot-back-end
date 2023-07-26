package pcrc.gotbetter.user.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.SocialAccount;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.SocialAccountRepository;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;

@Service
public class OAuthService implements OAuthOperationUseCase {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;

    public OAuthService(
        UserRepository userRepository,
        SocialAccountRepository socialAccountRepository,
        JwtProvider jwtProvider
    ) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public TokenInfo oAuthLogin(OAuthLoginCommand command) {
        User findUser = userRepository.findByEmail(command.getEmail());
        SocialAccount findSocialAccount = socialAccountRepository.findByTypeAndId(ProviderType.GOOGLE, command.getId());

        if ((findUser == null && findSocialAccount == null)
            || (findUser != null && findSocialAccount == null)) {
            // 아예 아무것도 안 한 경우 or 자체 로그인을 한 경우
            if (findUser == null) {
                // 처음 소셜 로그인 하는 경우
                findUser = User.builder()
                    .username(command.getName())
                    .email(command.getEmail())
                    // .profile()
                    .build();
                findUser.updateById("-1");
                userRepository.save(findUser);
            }
            SocialAccount saveSocialAccount = SocialAccount.builder()
                .userId(findUser.getUserId())
                .providerType(ProviderType.GOOGLE)
                .providerId(command.getId())
                .build();
            saveSocialAccount.updateById(findUser.getUserId().toString());
            socialAccountRepository.save(saveSocialAccount);
        } else if (findUser != null) {
            // 소셜 로그인을 한 적이 있는 경우
            if (!Objects.equals(findUser.getUserId(), findSocialAccount.getUserId())) {
                throw new GotBetterException(MessageType.BAD_REQUEST);
            }
        } else {
            // 문제있음
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }

        // 토큰 만들기
        TokenInfo tokenInfo = jwtProvider.generateToken(findUser.getUserId().toString());

        findUser.updateRefreshToken(tokenInfo.getRefreshToken());
        findUser.updateById(findUser.getUserId().toString());
        userRepository.save(findUser);

        return tokenInfo;
    }
}
