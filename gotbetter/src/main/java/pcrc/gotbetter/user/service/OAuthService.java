package pcrc.gotbetter.user.service;

import org.springframework.stereotype.Service;
import pcrc.gotbetter.user.data_access.entity.SocialAccount;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.SocialAccountRepository;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;

@Service
public class OAuthService implements OAuthOperationUseCase {
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;

    public OAuthService(UserRepository userRepository, SocialAccountRepository socialAccountRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.socialAccountRepository = socialAccountRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public TokenInfo oAuthLogin(OAuthLoginCommand command) {
        Long userId;
        // 유저 정보 저장 (insert or select)
        if (socialAccountRepository.existsByProviderTypeAndProviderId(ProviderType.GOOGLE, command.getId())) {
            User findUser = userRepository.findByEmail(command.getEmail());
            userId = findUser.getUserId();
        } else {
            User findUser = userRepository.findByEmail(command.getEmail());
            if (findUser == null) {
                findUser = User.builder()
                        .username(command.getName())
                        .email(command.getEmail())
//                        .profile()
                        .build();
                userRepository.save(findUser);
            }
            SocialAccount saveSocialAccount = SocialAccount.builder()
                    .userId(findUser.getUserId())
                    .providerType(ProviderType.GOOGLE)
                    .providerId(command.getId())
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
