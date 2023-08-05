package pcrc.gotbetter.common.service;

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
import org.springframework.stereotype.Service;

import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;
import pcrc.gotbetter.common.data_access.repository.CommonCodeRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class CommonCodeService implements CommonCodeReadUseCase, CommonCodeOperationUseCase {
	private final CommonCodeRepository commonCodeRepository;
	private final UserRepository userRepository;
	@Value("${local.default.loading.image}")
	String LOADING_LOCAL_DEFAULT_IMG;
	@Value("${server.default.loading.image}")
	String LOADING_SERVER_DEFAULT_IMG;

	@Autowired
	public CommonCodeService(CommonCodeRepository commonCodeRepository, UserRepository userRepository) {
		this.commonCodeRepository = commonCodeRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<FindCommonCodeResult> getRoomCategories(Boolean admin) throws IOException {
		List<FindCommonCodeResult> results = new ArrayList<>();
		List<CommonCode> roomCategories = commonCodeRepository.findListByGroupCode("ROOM_CATEGORY");

		for (CommonCode c : roomCategories) {
			String imageBytes;
			String updatedBy = null;

			try {
				imageBytes = Base64.getEncoder().encodeToString(Files.readAllBytes(
					Paths.get(c.getAttribute1())));
			} catch (Exception e) {
				String os = System.getProperty("os.name").toLowerCase();
				String dir = os.contains("win") ? LOADING_LOCAL_DEFAULT_IMG : LOADING_SERVER_DEFAULT_IMG;
				imageBytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
			}
			c.changeImageToByte(imageBytes);
			if (admin) {
				User findUser = userRepository.findByUserId(Long.parseLong(c.getUpdatedById())).orElseThrow(() -> {
					throw new GotBetterException(MessageType.NOT_FOUND);
				});

				updatedBy = findUser.getUsername();
			}
			results.add(FindCommonCodeResult.findByCommonCode(c, updatedBy));
		}
		return results;
	}

	@Override
	public List<FindCommonCodeResult> getRules(Boolean admin) {
		List<FindCommonCodeResult> results = new ArrayList<>();
		List<CommonCode> rules = commonCodeRepository.findListByGroupCode("RULE");

		for (CommonCode c : rules) {
			String updatedBy = null;

			if (admin) {
				User findUser = userRepository.findByUserId(Long.parseLong(c.getUpdatedById())).orElseThrow(() -> {
					throw new GotBetterException(MessageType.NOT_FOUND);
				});

				updatedBy = findUser.getUsername();
			}
			results.add(FindCommonCodeResult.findByCommonCode(c, updatedBy));
		}
		return results;
	}

	@Override
	public void updateCommonInfo(CommonCodeUpdateCommand command) {
		User requestUser = userRepository.findByUserId(getCurrentUserId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (requestUser.getRoleType() != RoleType.MAIN_ADMIN && requestUser.getRoleType() != RoleType.ADMIN) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}

		CommonCode commonCode = commonCodeRepository.findByCommonCodeId(CommonCodeId.builder()
			.groupCode(command.getGroupCode())
			.code(command.getCode())
			.build());

		if (commonCode == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		commonCode.updateInfo(command.getCodeDescription(),
			Objects.equals(command.getGroupCode(),
				"ROOM_CATEGORY") ? commonCode.getAttribute1() : command.getAttribute1(), command.getAttribute2());
		commonCodeRepository.save(commonCode);
	}
}
