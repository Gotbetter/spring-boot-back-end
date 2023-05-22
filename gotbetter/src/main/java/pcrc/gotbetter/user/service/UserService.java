package pcrc.gotbetter.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.UserSet;
import pcrc.gotbetter.user.data_access.repository.UserSetRepository;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.TokenInfo;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserSetRepository userSetRepository;
    @Value("${local.default.profile.path}")
    String DEFAULT_PROFILE_LOCAL_PATH;
    @Value("${server.default.profile.path}")
    String DEFAULT_PROFILE_SERVER_PATH;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       JwtProvider jwtProvider, UserSetRepository userSetRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.userSetRepository = userSetRepository;
    }

    @Override
    public FindUserResult createUser(UserCreateCommand command) {
        // authId 이미 있는지 검사
        validateUserAuthId(command.getAuthId());
        // email 있는지 확인하고 있으면 userset확인하고 이것도 있으면 존재하는 유저라고 판단
        Long userId = validateUserEmail(command.getEmail());

        if (userId != null) {
            validateAlreadyJoin(userId);
            // username update
            userRepository.updateUsername(userId, command.getUsername());
        } else {
            User saveUser = User.builder()
                    .username(command.getUsername())
                    .email(command.getEmail())
                    .build();
            userRepository.save(saveUser);
            userId = saveUser.getUserId();
        }

        String encodePassword = passwordEncoder.encode(command.getPassword());
        UserSet savedUserSet = UserSet.builder()
                .userId(userId)
                .authId(command.getAuthId())
                .password(encodePassword)
                .build();
        userSetRepository.save(savedUserSet);

        User returnUser = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .build();
        UserSet userSet = UserSet.builder()
                .authId(command.getAuthId())
                .build();
        return FindUserResult.findByUser(returnUser, userSet, TokenInfo.builder().build());
    }

    @Override
    public FindUserResult verifyId(String authId) {
        validateUserAuthId(authId);
        UserSet userSet = UserSet.builder()
                .authId(authId)
                .build();
        return FindUserResult.findByUser(User.builder().build(), userSet, TokenInfo.builder().build());
    }

    @Override
    public FindUserResult loginUser(UserFindQuery query) {
        // 아이디와 비번이 매치되는 유저가 있는지 확인
        UserSet findUserSet = validateFindUserSet(query);
        // jwt
        TokenInfo tokenInfo = jwtProvider.generateToken(findUserSet.getUserId().toString());

        userRepository.updateRefreshToken(findUserSet.getUserId(), tokenInfo.getRefreshToken());
        return FindUserResult.findByUser(User.builder().build(), UserSet.builder().build(), tokenInfo);
    }

    @Override
    public FindUserResult getUserInfo() throws IOException {
        User findUser = validateUser();
        UserSet findUserSet = userSetRepository.findByUserId(findUser.getUserId());
        String bytes;

        try {
            bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(
                    Paths.get(findUser.getProfile())));
        } catch (Exception e) {
            String os = System.getProperty("os.name").toLowerCase();
            String dir = os.contains("win") ? DEFAULT_PROFILE_LOCAL_PATH : DEFAULT_PROFILE_SERVER_PATH;
            bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
        }
        User user = User.builder()
                .userId(findUser.getUserId())
                .username(findUser.getUsername())
                .email(findUser.getEmail())
                .profile(bytes)
                .build();
        UserSet userSet = UserSet.builder()
                .authId(findUserSet != null ? findUserSet.getAuthId() : "")
                .build();
        return FindUserResult.findByUser(user, userSet, TokenInfo.builder().build());
    }

    /**
     * validate
     */
    private UserSet validateFindUserSet(UserFindQuery query) {
        UserSet findUserSet = userSetRepository.findByAuthId(query.getAuthId());

        if (findUserSet == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (!passwordEncoder.matches(query.getPassword(), findUserSet.getPassword())) {
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

    private Long validateUserEmail(String email) {
        return userRepository.findUserIdByEmail(email);
    }

    private void validateAlreadyJoin(Long userId) {
        if (userSetRepository.existsByUserId(userId)) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
    }

    private void validateUserAuthId(String authId) {
        if (userSetRepository.existsByAuthId(authId)) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
    }
}
