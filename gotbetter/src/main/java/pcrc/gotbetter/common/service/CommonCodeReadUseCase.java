package pcrc.gotbetter.common.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.common.data_access.entity.CommonCode;

public interface CommonCodeReadUseCase {
	List<FindCommonCodeResult> getRoomCategories(Boolean admin) throws IOException;

	List<FindCommonCodeResult> getRules(Boolean admin);

	@Getter
	@ToString
	@Builder
	class FindCommonCodeResult {
		private final String code;
		private final String codeDescription;
		private final String attribute1;
		private final String attribute2;
		private final String attribute3;
		private final String updatedDate;
		private final String updatedBy;
		private final Integer commonOrder;

		public static FindCommonCodeResult findByCommonCode(CommonCode commonCode, String updatedBy) {
			return FindCommonCodeResult.builder()
				.code(commonCode.getCommonCodeId().getCode())
				.codeDescription(commonCode.getCodeDescription())
				.attribute1(commonCode.getAttribute1())
				.attribute2(commonCode.getAttribute2())
				.attribute3(commonCode.getAttribute3())
				.updatedDate(updatedBy == null ? null : commonCode.getUpdatedDate().toLocalDate().toString())
				.updatedBy(updatedBy)
				.commonOrder(commonCode.getCommonOrder())
				.build();
		}
	}
}
