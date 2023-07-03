package pcrc.gotbetter.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;
import pcrc.gotbetter.common.data_access.repository.CommonCodeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class CommonCodeService implements CommonCodeReadUseCase {
    private final CommonCodeRepository commonCodeRepository;
    @Value("${local.default.image.path}")
    String DEFAULT_LOCAL_PATH;
    @Value("${server.default.image.path}")
    String DEFAULT_SERVER_PATH;

    @Autowired
    public CommonCodeService(CommonCodeRepository commonCodeRepository) {
        this.commonCodeRepository = commonCodeRepository;
    }

    @Override
    public List<FindCommonCodeResult> getRoomCategories() throws IOException {
        List<FindCommonCodeResult> results = new ArrayList<>();
        List<CommonCode> roomCategories = commonCodeRepository.findListByGroupCode("ROOM_CATEGORY");

        for (CommonCode c : roomCategories) {
            String imageBytes;
            try {
                imageBytes = Base64.getEncoder().encodeToString(Files.readAllBytes(
                        Paths.get(c.getAttribute1())));
            } catch (Exception e) {
                String os = System.getProperty("os.name").toLowerCase();
                String dir = os.contains("win") ? DEFAULT_LOCAL_PATH : DEFAULT_SERVER_PATH;
                imageBytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
            }
            CommonCode roomCategory = CommonCode.builder()
                    .commonCodeId(CommonCodeId.builder()
                            .code(c.getCommonCodeId().getCode()).build())
                    .codeDescription(c.getCodeDescription())
                    .attribute1(imageBytes)
                    .build();
            results.add(FindCommonCodeResult.findByCommonCode(roomCategory));
        }
        return results;
    }

    @Override
    public List<FindCommonCodeResult> getRules() {
        List<FindCommonCodeResult> results = new ArrayList<>();
        List<CommonCode> rules = commonCodeRepository.findListByGroupCode("RULE");

        for (CommonCode c : rules) {
            results.add(FindCommonCodeResult.findByCommonCode(c));
        }
        return results;
    }
}
