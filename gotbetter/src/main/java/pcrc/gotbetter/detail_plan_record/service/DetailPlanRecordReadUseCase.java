package pcrc.gotbetter.detail_plan_record.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;

public interface DetailPlanRecordReadUseCase {

	List<FindDetailPlanRecordResult> getRecordList(Long detailPlanId) throws IOException;

	@Getter
	@ToString
	@Builder
	class FindDetailPlanRecordResult {
		private final Long recordId;
		private final String recordTitle;
		private final String recordBody;
		private final String recordPhoto;
		private final String lastUpdateDate;

		public static FindDetailPlanRecordResult findByDetailPlanRecord(DetailPlanRecord record) {
			return FindDetailPlanRecordResult.builder()
				.recordId(record.getRecordId())
				.recordTitle(record.getRecordTitle())
				.recordBody(record.getRecordBody())
				.recordPhoto(record.getRecordPhoto())
				.lastUpdateDate(record.getUpdatedDate().toString().split("\\.")[0].replace("T", " "))
				.build();
		}

		public static FindDetailPlanRecordResult findByDetailPlanRecord(DetailPlanRecord record, String bytes) {
			return FindDetailPlanRecordResult.builder()
				.recordId(record.getRecordId())
				.recordTitle(record.getRecordTitle())
				.recordBody(record.getRecordBody())
				.recordPhoto(bytes)
				.lastUpdateDate(record.getUpdatedDate().toString().split("\\.")[0].replace("T", " "))
				.build();
		}
	}
}
