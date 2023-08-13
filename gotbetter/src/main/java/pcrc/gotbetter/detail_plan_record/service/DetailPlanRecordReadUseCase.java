package pcrc.gotbetter.detail_plan_record.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;

public interface DetailPlanRecordReadUseCase {

	List<FindDetailPlanRecordResult> getRecordList(RecordsFindQuery query) throws IOException;

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class RecordsFindQuery {
		private final Long detailPlanId;
		private final Boolean admin;
	}

	@Getter
	@ToString
	@Builder
	class FindDetailPlanRecordResult {
		private final Long recordId;
		private final String recordTitle;
		private final String recordBody;
		private final String recordPhoto;
		private final String lastUpdateDate;
		// for admin
		private final String createdDate;

		public static FindDetailPlanRecordResult findByDetailPlanRecord(DetailPlanRecord record) {
			return FindDetailPlanRecordResult.builder()
				.recordId(record.getRecordId())
				.recordTitle(record.getRecordTitle())
				.recordBody(record.getRecordBody())
				.recordPhoto(record.getRecordPhoto())
				.lastUpdateDate(record.getUpdatedDate().toString().split("\\.")[0].replace("T", " "))
				.build();
		}

		public static FindDetailPlanRecordResult findByDetailPlanRecord(
			DetailPlanRecord record,
			String bytes,
			Boolean admin
		) {
			return FindDetailPlanRecordResult.builder()
				.recordId(record.getRecordId())
				.recordTitle(record.getRecordTitle())
				.recordBody(record.getRecordBody())
				.recordPhoto(bytes)
				.lastUpdateDate(record.getUpdatedDate().toString().split("\\.")[0].replace("T", " "))
				.createdDate(admin ? record.getCreatedDate().toLocalDate().toString() : null)
				.build();
		}
	}
}
