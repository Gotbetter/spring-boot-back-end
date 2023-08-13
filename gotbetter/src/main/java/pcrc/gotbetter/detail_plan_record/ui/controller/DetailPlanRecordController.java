package pcrc.gotbetter.detail_plan_record.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.detail_plan_record.service.DetailPlanRecordOperationUseCase;
import pcrc.gotbetter.detail_plan_record.service.DetailPlanRecordReadUseCase;
import pcrc.gotbetter.detail_plan_record.ui.request_body.DetailPlanRecordRequest;
import pcrc.gotbetter.detail_plan_record.ui.request_body.DetailRecordAdminRequest;
import pcrc.gotbetter.detail_plan_record.ui.view.DetailPlanRecordView;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

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

	@PostMapping(value = "")
	public ResponseEntity<DetailPlanRecordView> createRecord(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@Valid @ModelAttribute DetailPlanRecordRequest request
	) throws IOException {

		log.info("\"CREATE A DETAIL PLAN RECORD\"");

		if (request.getRecord_photo().isEmpty()) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}

		var command = DetailPlanRecordOperationUseCase.DetailPlanRecordCreateCommand.builder()
			.detailPlanId(detail_plan_id)
			.recordTitle(request.getRecord_title())
			.recordBody(request.getRecord_body())
			.recordPhoto(request.getRecord_photo())
			.admin(false)
			.build();
		DetailPlanRecordReadUseCase.FindDetailPlanRecordResult result = detailPlanRecordOperationUseCase.createRecord(
			command);

		return ResponseEntity.created(null).body(DetailPlanRecordView.builder().detailPlanRecordResult(result).build());
	}

	@PostMapping(value = "/admin")
	public ResponseEntity<DetailPlanRecordView> createRecordAdmin(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@Valid @RequestBody DetailRecordAdminRequest request
	) throws IOException {

		log.info("\"CREATE A DETAIL PLAN RECORD (admin)\"");

		var command = DetailPlanRecordOperationUseCase.DetailPlanRecordCreateCommand.builder()
			.detailPlanId(detail_plan_id)
			.recordTitle(request.getRecord_title())
			.recordBody(request.getRecord_body())
			.admin(true)
			.build();
		DetailPlanRecordReadUseCase.FindDetailPlanRecordResult result = detailPlanRecordOperationUseCase.createRecord(
			command);

		return ResponseEntity.created(null).body(DetailPlanRecordView.builder().detailPlanRecordResult(result).build());
	}

	@GetMapping(value = "")
	public ResponseEntity<List<DetailPlanRecordView>> getRecordList(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@RequestParam(name = "admin", required = false) Boolean admin
	) throws IOException {

		log.info("\"GET A DETAIL PLAN RECORD LIST\"");

		var query = DetailPlanRecordReadUseCase.RecordsFindQuery.builder()
			.detailPlanId(detail_plan_id)
			.admin(admin != null && admin)
			.build();
		List<DetailPlanRecordReadUseCase.FindDetailPlanRecordResult> records = detailPlanRecordReadUseCase.getRecordList(
			query);
		List<DetailPlanRecordView> detailPlanRecordViews = new ArrayList<>();

		for (DetailPlanRecordReadUseCase.FindDetailPlanRecordResult record : records) {
			detailPlanRecordViews.add(DetailPlanRecordView.builder().detailPlanRecordResult(record).build());
		}
		return ResponseEntity.ok(detailPlanRecordViews);
	}

	@PatchMapping(value = "/{record_id}")
	public ResponseEntity<DetailPlanRecordView> updateRecord(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@PathVariable(value = "record_id") Long record_id,
		@Valid @ModelAttribute DetailPlanRecordRequest request
	) {

		log.info("\"UPDATE THE DETAIL PLAN RECORD\"");

		if (request.getRecord_photo().isEmpty()) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}

		var command = DetailPlanRecordOperationUseCase.DetailPlanRecordUpdateCommand.builder()
			.detailPlanId(detail_plan_id)
			.recordId(record_id)
			.recordTitle(request.getRecord_title())
			.recordBody(request.getRecord_body())
			.recordPhoto(request.getRecord_photo())
			.admin(false)
			.build();
		DetailPlanRecordReadUseCase.FindDetailPlanRecordResult result = detailPlanRecordOperationUseCase.updateRecord(
			command);

		return ResponseEntity.ok(DetailPlanRecordView.builder().detailPlanRecordResult(result).build());
	}

	@PatchMapping(value = "/{record_id}/admin")
	public ResponseEntity<DetailPlanRecordView> updateRecordAdmin(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@PathVariable(value = "record_id") Long record_id,
		@Valid @RequestBody DetailRecordAdminRequest request
	) {

		log.info("\"UPDATE THE DETAIL PLAN RECORD (admin)\"");

		var command = DetailPlanRecordOperationUseCase.DetailPlanRecordUpdateCommand.builder()
			.detailPlanId(detail_plan_id)
			.recordId(record_id)
			.recordTitle(request.getRecord_title())
			.recordBody(request.getRecord_body())
			.admin(true)
			.build();
		DetailPlanRecordReadUseCase.FindDetailPlanRecordResult result = detailPlanRecordOperationUseCase.updateRecord(
			command);

		return ResponseEntity.ok(DetailPlanRecordView.builder().detailPlanRecordResult(result).build());
	}

	@DeleteMapping(value = "/{record_id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRecord(@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@PathVariable(value = "record_id") Long record_id) {

		log.info("\"DELETE THE DETAIL PLAN RECORD\"");

		var command = DetailPlanRecordOperationUseCase.DetailPlanRecordDeleteCommand.builder()
			.detailPlanId(detail_plan_id)
			.recordId(record_id)
			.build();
		detailPlanRecordOperationUseCase.deleteRecord(command);
	}
}
