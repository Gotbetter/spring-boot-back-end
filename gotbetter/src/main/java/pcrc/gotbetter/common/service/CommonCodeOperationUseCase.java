package pcrc.gotbetter.common.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface CommonCodeOperationUseCase {
	void updateCommonInfo(CommonCodeUpdateCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class CommonCodeUpdateCommand {
		private String groupCode;
		private String code;
		private String codeDescription;
		private String attribute1;
		private String attribute2;
	}
}
