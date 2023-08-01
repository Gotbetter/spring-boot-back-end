package pcrc.gotbetter.user.login_method.oauth;

import java.util.Objects;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
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
public class OAuthWebService {
	private final OauthInterface oauthInterface;
	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final JwtProvider jwtProvider;

	@Autowired
	public OAuthWebService(
		OauthInterface oauthInterface,
		UserRepository userRepository,
		SocialAccountRepository socialAccountRepository,
		JwtProvider jwtProvider
	) {
		this.oauthInterface = oauthInterface;
		this.userRepository = userRepository;
		this.socialAccountRepository = socialAccountRepository;
		this.jwtProvider = jwtProvider;
	}

	public String forCodeUrl() {
		return oauthInterface.getOAuthRedirectURL();
	}

	public TokenInfo oAuthLogin(String code) throws JsonProcessingException, ParseException {
		// get access token
		GoogleOAuthToken googleOAuthToken = oauthInterface.requestAccessToken(code);
		// get user info
		GoogleUser googleUser = oauthInterface.requestUserInfo(googleOAuthToken);

		User findUser = userRepository.findByEmail(googleUser.email);
		SocialAccount findSocialAccount = socialAccountRepository.findByTypeAndId(ProviderType.GOOGLE, googleUser.id);

		if ((findUser == null && findSocialAccount == null)
			|| (findUser != null && findSocialAccount == null)) {
			// 아예 아무것도 안 한 경우 or 자체 로그인을 한 경우
			if (findUser == null) {
				// 처음 소셜 로그인 하는 경우
				findUser = User.builder()
					.username(googleUser.name)
					.email(googleUser.email)
					// .profile()
					.build();
				userRepository.save(findUser);
			}
			SocialAccount saveSocialAccount = SocialAccount.builder()
				.userId(findUser.getUserId())
				.providerType(ProviderType.GOOGLE)
				.providerId(googleUser.id)
				.build();
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
		TokenInfo tokenInfo = jwtProvider.generateToken(findUser.getUserId().toString(), findUser.getRoleType());

		findUser.updateRefreshToken(tokenInfo.getRefreshToken());
		userRepository.save(findUser);
		return tokenInfo;
	}
}