package pcrc.gotbetter.common.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.common.data_access.entity.CommonCode;

import java.io.IOException;
import java.util.List;

public interface CommonCodeReadUseCase {
    List<FindCommonCodeResult> getRoomCategories() throws IOException;

    @Getter
    @ToString
    @Builder
    class FindCommonCodeResult {
        private final String code;
        private final String codeDescription;
        private final String attribute1;
        private final String attribute2;
        private final String attribute3;

        public static FindCommonCodeResult findByCommonCode(CommonCode commonCode) {
            return FindCommonCodeResult.builder()
                    .code(commonCode.getCommonCodeId().getCode())
                    .codeDescription(commonCode.getCodeDescription())
                    .attribute1(commonCode.getAttribute1())
                    .attribute2(commonCode.getAttribute2())
                    .attribute3(commonCode.getAttribute3())
                    .build();
        }
    }
}
