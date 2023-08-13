package pcrc.gotbetter.detail_plan_record.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface DetailPlanRecordOperationUseCase {

	DetailPlanRecordReadUseCase.FindDetailPlanRecordResult createRecord(DetailPlanRecordCreateCommand command) throws
		IOException;

	DetailPlanRecordReadUseCase.FindDetailPlanRecordResult updateRecord(DetailPlanRecordUpdateCommand command);

	void deleteRecord(DetailPlanRecordDeleteCommand command);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanRecordCreateCommand {
		private final Long detailPlanId;
		private final String recordTitle;
		private final String recordBody;
		private final MultipartFile recordPhoto;
		private final Boolean admin;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanRecordUpdateCommand {
		private final Long detailPlanId;
		private final Long recordId;
		private final String recordTitle;
		private final String recordBody;
		private final MultipartFile recordPhoto;
		private final Boolean admin;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class DetailPlanRecordDeleteCommand {
		private final Long detailPlanId;
		private final Long recordId;
		private final Boolean admin;
	}
}
