package pcrc.gotbetter.detail_plan_record.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanRecordOperationUseCase {

	DetailPlanRecordReadUseCase.FindDetailPlanRecordResult createRecord(DetailPlanRecordCreateCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanRecordCreateCommand {
		private final Long detailPlanId;
		private final String recordTitle;
		private final String recordBody;
		private final String recordPhoto;
	}
}
