package pcrc.gotbetter.detail_plan_record.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.detail_plan_record.service.DetailPlanRecordOperationUseCase;
import pcrc.gotbetter.detail_plan_record.service.DetailPlanRecordReadUseCase;
import pcrc.gotbetter.detail_plan_record.ui.request_body.DetailPlanRecordRequest;
import pcrc.gotbetter.detail_plan_record.ui.view.DetailPlanRecordView;

@Slf4j
@RestController
@RequestMapping(value = "/details/{detail_plan_id}/records")
public class DetailPlanRecordController {

	private final DetailPlanRecordOperationUseCase detailPlanRecordOperationUseCase;
	private final DetailPlanRecordReadUseCase detailPlanRecordReadUseCase;

	@Autowired
	public DetailPlanRecordController(DetailPlanRecordOperationUseCase detailPlanRecordOperationUseCase,
		DetailPlanRecordReadUseCase detailPlanRecordReadUseCase) {
		this.detailPlanRecordOperationUseCase = detailPlanRecordOperationUseCase;
		this.detailPlanRecordReadUseCase = detailPlanRecordReadUseCase;
	}

	@PostMapping
	public ResponseEntity<DetailPlanRecordView> createRecord(@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@Valid @RequestBody DetailPlanRecordRequest request) {

		log.info("\"CREATE A DETAIL PLAN RECORD\"");

		var command = DetailPlanRecordOperationUseCase.DetailPlanRecordCreateCommand.builder()
			.detailPlanId(detail_plan_id)
			.recordTitle(request.getRecord_title())
			.recordBody(request.getRecord_body())
			.recordPhoto(request.getRecord_photo())
			.build();
		DetailPlanRecordReadUseCase.FindDetailPlanRecordResult result = detailPlanRecordOperationUseCase.createRecord(command);

		return ResponseEntity.created(null).body(DetailPlanRecordView.builder().detailPlanRecordResult(result).build());

	}
}
