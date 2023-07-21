package pcrc.gotbetter.detail_plan_record.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.detail_plan_record.service.DetailPlanRecordReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailPlanRecordView {
	private final Long record_id;
	private final String record_title;
	private final String record_body;
	private final String record_photo;
	private final String last_update_date;

	@Builder
	public DetailPlanRecordView(DetailPlanRecordReadUseCase.FindDetailPlanRecordResult detailPlanRecordResult) {
		this.record_id = detailPlanRecordResult.getRecordId();
		this.record_title = detailPlanRecordResult.getRecordTitle();
		this.record_body = detailPlanRecordResult.getRecordBody();
		this.record_photo = detailPlanRecordResult.getRecordPhoto();
		this.last_update_date = detailPlanRecordResult.getLastUpdateDate();
	}
}
