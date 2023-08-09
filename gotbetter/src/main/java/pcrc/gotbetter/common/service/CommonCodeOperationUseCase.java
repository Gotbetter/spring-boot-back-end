package pcrc.gotbetter.common.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface CommonCodeOperationUseCase {
	void updateCommonInfo(CommonCodeCommand command);

	void createCommonInfo(CommonCodeCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class CommonCodeCommand {
		private String groupCode;
		private String code;
		private String codeDescription;
		private String attribute1;
		private String attribute2;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class CommonCodeDeleteCommand {
		private String groupCode;
		private String code;
	}
}
