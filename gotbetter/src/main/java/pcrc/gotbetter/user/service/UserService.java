package pcrc.gotbetter.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.setting.security.JWT.JwtProvider;
import pcrc.gotbetter.setting.security.JWT.TokenInfo;
import pcrc.gotbetter.user.data_access.domain.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class UserService implements UserOperationUseCase, UserReadUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    @Value("${local.default.profile.path}")
    String default_profile_local_path;
    @Value("${server.default.profile.path}")
    String default_profile_server_path;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       JwtProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public FindUserResult createUser(UserCreateCommand command) {
        validateDuplicateUser(command.getAuth_id(), command.getEmail());

        String encodePassword = passwordEncoder.encode(command.getPassword());
        User saveUser = User.builder()
                .authId(command.getAuth_id())
                .password(encodePassword)
                .usernameNick(command.getUsername())
                .email(command.getEmail())
                .build();
        userRepository.save(saveUser);

        User returnUser = User.builder()
                .authId(command.getAuth_id())
                .usernameNick(command.getUsername())
                .email(command.getEmail())
                .build();
        return FindUserResult.findByUser(returnUser, TokenInfo.builder().build());
    }

    @Override
    public FindUserResult verifyId(String auth_id) {
        validateDuplicateUser(auth_id, null);
        User user = User.builder()
                .authId(auth_id)
                .build();
        return FindUserResult.findByUser(user, TokenInfo.builder().build());
    }

    @Override
    public FindUserResult loginUser(UserFindQuery query) throws IOException {
        // 아이디와 비번이 매치되는 유저가 있는지 확인
        User findUser = validateFindUser(query);

        String bytes;
        try {
            bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(
                    Paths.get(findUser.getProfile())));
        } catch (Exception e) {
            String os = System.getProperty("os.name").toLowerCase();
            String dir = os.contains("win") ? default_profile_local_path : default_profile_server_path;
            bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
        }

        // jwt
        TokenInfo tokenInfo = jwtProvider.generateToken(findUser.getAuthId());
        userRepository.updateRefreshToken(findUser.getAuthId(), tokenInfo.getRefreshToken());

        User user = User.builder()
                .authId(findUser.getAuthId())
                .usernameNick(findUser.getUsernameNick())
                .email(findUser.getEmail())
                .profile(bytes)
                .build();
        return FindUserResult.findByUser(user, tokenInfo);
    }

    private void validateDuplicateUser(String auth_id, String email) {
        // 이미 있는 아이디인지 또는 이미 있는 이메일인지 확인
        if (userRepository.existsByAuthidOrEmail(auth_id, email)) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
    }

    private User validateFindUser(UserFindQuery query) {
        User findUser = userRepository.findByAuthId(query.getAuth_id())
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });

        if (!passwordEncoder.matches(query.getPassword(), findUser.getPassword())) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        return findUser;
    }
}
