package pcrc.gotbetter.user.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.SocialAccount;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.data_access.repository.SocialAccountRepository;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.data_access.repository.UserSetRepository;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Slf4j
@Service
public class OAuthService implements OAuthOperationUseCase {
	@Value("${server.default.profile.image}")
	String PROFILE_SERVER_DEFAULT_IMG;
	@Value("${server.default.base.profile.path}")
	String PROFILE_SERVER_BASE_PATH;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final UserSetRepository userSetRepository;

	public OAuthService(
		UserRepository userRepository,
		SocialAccountRepository socialAccountRepository,
		JwtProvider jwtProvider,
		UserSetRepository userSetRepository) {
		this.userRepository = userRepository;
		this.socialAccountRepository = socialAccountRepository;
		this.jwtProvider = jwtProvider;
		this.userSetRepository = userSetRepository;
	}

	@Override
	public TokenInfo oAuthLogin(OAuthLoginCommand command) throws IOException {
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
					.roleType(RoleType.USER)
					// .profile()
					.build();
				findUser.updateById("-1");
				userRepository.save(findUser);
				boolean useLetters = true;
				boolean useNumbers = true;
				int randomStrLen = 20;
				String roomCode = RandomStringUtils.random(randomStrLen, useLetters, useNumbers);

				UserSet userSet = UserSet.builder()
					.userId(findUser.getUserId())
					.authId(command.getEmail())
					.password(roomCode)
					.build();
				userSet.updateById(findUser.getUserId().toString());
				userSetRepository.save(userSet);
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
		if (Objects.equals(findUser.getProfile(), PROFILE_SERVER_DEFAULT_IMG)) {
			URL url = new URL(command.getPicture());
			BufferedImage img = ImageIO.read(url);
			String storePath = PROFILE_SERVER_BASE_PATH + findUser.getUserId() + ".png";
			File file = deleteImages(storePath);
			ImageIO.write(img, "png", file); // 파일 저장
			findUser.updateProfile(storePath);
			userRepository.save(findUser);
		}
		
		// 토큰 만들기
		TokenInfo tokenInfo = jwtProvider.generateToken(findUser.getUserId().toString(), findUser.getRoleType());

		findUser.updateRefreshToken(tokenInfo.getRefreshToken());
		findUser.updateById(findUser.getUserId().toString());
		userRepository.save(findUser);

		return tokenInfo;
	}

	private File deleteImages(String storePath) {
		File storeFile = new File(storePath);

		if (!storeFile.exists()) {
			try {
				storeFile.mkdirs();
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		return storeFile;
	}
}
