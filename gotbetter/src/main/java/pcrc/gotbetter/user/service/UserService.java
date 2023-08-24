package pcrc.gotbetter.user.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.dto.UserDto;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.data_access.repository.UserSetRepository;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {
	@Value("${local.default.profile.image}")
	String PROFILE_LOCAL_DEFAULT_IMG;
	@Value("${server.default.profile.image}")
	String PROFILE_SERVER_DEFAULT_IMG;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final UserSetRepository userSetRepository;

	@Autowired
	public UserService(
		PasswordEncoder passwordEncoder,
		UserRepository userRepository,
		JwtProvider jwtProvider,
		UserSetRepository userSetRepository
	) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
		this.userSetRepository = userSetRepository;
	}

	@Override
	public FindUserResult createUser(UserCreateCommand command) {
		// authId 이미 있는지 검사
		validateUserAuthId(command.getAuthId());
		// email이 있는지 확인하고, userset에 연결된 계정이 있는지 확인
		User findUser = userRepository.findByEmail(command.getEmail());

		if (findUser != null) {
			// 이미 가입한 유저인지 검사
			if (userSetRepository.existsByUserId(findUser.getUserId())) {
				throw new GotBetterException(MessageType.CONFLICT);
			}
			// 소셜 로그인으로 가입되어 있는 경우
			findUser.updateUsername(command.getUsername());
			findUser.updateById(findUser.getUserId().toString());
			userRepository.save(findUser);
		} else {
			// 아무것도 가입되어있지 않은 경우
			User saveUser = User.builder()
				.username(command.getUsername())
				.email(command.getEmail())
				.roleType(RoleType.USER)
				.profile(PROFILE_SERVER_DEFAULT_IMG)
				.build();
			saveUser.updateById("-1");
			userRepository.save(saveUser);
			findUser = saveUser;
		}

		String encodePassword = passwordEncoder.encode(command.getPassword());
		UserSet savedUserSet = UserSet.builder()
			.userId(findUser.getUserId())
			.authId(command.getAuthId())
			.password(encodePassword)
			.build();
		savedUserSet.updateById(findUser.getUserId().toString());
		userSetRepository.save(savedUserSet);
		return FindUserResult.findByUser(findUser, savedUserSet, TokenInfo.builder().build());
	}

	@Override
	public FindUserResult verifyId(String authId) {
		validateUserAuthId(authId);
		UserSet userSet = UserSet.builder()
			.authId(authId)
			.build();
		return FindUserResult.findByUser(
			User.builder().build(),
			userSet,
			TokenInfo.builder().build()
		);
	}

	@Override
	public FindUserResult loginUser(UserFindQuery query) {
		// 아이디와 비번이 매치되는 유저가 있는지 확인
		UserSet findUserSet = validateFindUserSet(query);
		// User 테이블에서 User 객체 가져오기
		User findUser = userRepository.findByUserId(findUserSet.getUserId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
		// jwt
		TokenInfo tokenInfo = jwtProvider.generateToken(findUserSet.getUserId().toString(), findUser.getRoleType());

		if (query.getIsAdmin()
			&& (findUser.getRoleType() != RoleType.ADMIN && findUser.getRoleType() != RoleType.MAIN_ADMIN)) {
			throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
		}
		findUser.updateRefreshToken(tokenInfo.getRefreshToken());
		findUser.updateById(findUser.getUserId().toString());
		userRepository.save(findUser);
		return FindUserResult.findByUser(
			User.builder().build(),
			UserSet.builder().build(),
			tokenInfo
		);
	}

	@Override
	public FindUserResult getUserInfo() throws IOException {
		User findUser = validateUser();
		UserSet findUserSet = userSetRepository.findByUserId(findUser.getUserId());
		String bytes = getByteProfile(findUser.getProfile());
		User user = User.builder()
			.userId(findUser.getUserId())
			.username(findUser.getUsername())
			.email(findUser.getEmail())
			.profile(bytes)
			.build();
		UserSet userSet = UserSet.builder()
			.authId(findUserSet != null ? findUserSet.getAuthId() : "") // 소셜 로그인으로만 가입되어있는 경우
			.build();
		return FindUserResult.findByUser(user, userSet, TokenInfo.builder().build());
	}

	@Override
	public List<FindUserResult> getAllUserInfo() throws IOException {
		User requestUser = validateUser();

		if (requestUser.getRoleType() != RoleType.ADMIN && requestUser.getRoleType() != RoleType.MAIN_ADMIN) {
			throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
		}

		List<UserDto> allUsers = userRepository.findAllUserUserSet();
		List<FindUserResult> results = new ArrayList<>();

		for (UserDto userDto : allUsers) {
			User userInfo = userDto.getUser();
			UserSet userSetInfo = userDto.getUserSet();
			String bytes = getByteProfile(userInfo.getProfile());
			UserSet userSet = UserSet.builder()
				.authId(userSetInfo != null ? userSetInfo.getAuthId() : "") // 소셜 로그인으로만 가입되어있는 경우
				.build();
			results.add(FindUserResult.findByUsers(userInfo, userSet, bytes));
		}
		return results;
	}

	@Override
	public void changeAuthentication(UserAdminUpdateCommand command) {
		User requestUser = validateUser();

		if (requestUser.getRoleType() != RoleType.ADMIN && requestUser.getRoleType() != RoleType.MAIN_ADMIN) {
			throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
		}
		if (requestUser.getRoleType() != RoleType.MAIN_ADMIN) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}

		User findUser = userRepository.findByUserId(command.getUserId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (command.getApprove()) {
			if (findUser.getRoleType() == RoleType.ADMIN) {
				throw new GotBetterException(MessageType.CONFLICT);
			}
			findUser.updateRoleType(RoleType.ADMIN);
		} else {
			if (findUser.getRoleType() == RoleType.USER) {
				throw new GotBetterException(MessageType.CONFLICT);
			}
			findUser.updateRoleType(RoleType.USER);
		}
		userRepository.save(findUser);
	}

	@Override
	public void deleteUser(Long userId) {
		User requestUser = validateUser();
		User findUser = userRepository.findByUserId(userId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (Objects.equals(requestUser.getUserId(), findUser.getUserId())
			|| (requestUser.getRoleType() == RoleType.ADMIN || requestUser.getRoleType() == RoleType.MAIN_ADMIN)) {
			userRepository.delete(findUser);
			return;
		}
		throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
	}

	@Override
	public void logoutUser(Boolean isAdmin) {
		User findUser = validateUser();

		findUser.updateRefreshToken(null);
		userRepository.save(findUser);
	}

	@Override
	public void updateUserInfo(UserUpdateCommand command) {
		User requestUser = validateUser();
		User findUser = userRepository.findByUserId(command.getUserId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (Objects.equals(requestUser.getUserId(), findUser.getUserId())
			|| (requestUser.getRoleType() == RoleType.ADMIN || requestUser.getRoleType() == RoleType.MAIN_ADMIN)) {
			findUser.updateUsername(command.getUsername());
			userRepository.save(findUser);
			return;
		}
		throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
	}

	/**
	 * validate
	 */
	private void validateUserAuthId(String authId) {
		if (userSetRepository.existsByAuthId(authId)) {
			throw new GotBetterException(MessageType.CONFLICT);
		}
	}

	private UserSet validateFindUserSet(UserFindQuery query) {
		UserSet findUserSet = userSetRepository.findByAuthId(query.getAuthId());

		if (findUserSet == null
			|| !passwordEncoder.matches(query.getPassword(), findUserSet.getPassword())) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return findUserSet;
	}

	private User validateUser() {
		Long currentUserId = getCurrentUserId();
		return userRepository.findByUserId(currentUserId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
	}

	private String getByteProfile(String profile) throws IOException {
		String bytes;

		try {
			bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(profile)));
		} catch (Exception e) {
			String os = System.getProperty("os.name").toLowerCase();
			String dir =
				os.contains("win") ? PROFILE_LOCAL_DEFAULT_IMG : PROFILE_SERVER_DEFAULT_IMG;
			bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
		}
		return bytes;
	}
}
