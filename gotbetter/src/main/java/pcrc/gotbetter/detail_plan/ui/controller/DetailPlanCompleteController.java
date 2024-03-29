package pcrc.gotbetter.detail_plan.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.detail_plan.service.DetailPlanCompleteOperationUseCase;
import pcrc.gotbetter.detail_plan.service.DetailPlanReadUseCase;
import pcrc.gotbetter.detail_plan.ui.view.DetailPlanView;

@Slf4j
@RestController
@RequestMapping(value = "/plans/{plan_id}/details/{detail_plan_id}")
public class DetailPlanCompleteController {

	private final DetailPlanCompleteOperationUseCase detailPlanCompleteOperationUseCase;

	@Autowired
	public DetailPlanCompleteController(DetailPlanCompleteOperationUseCase detailPlanCompleteOperationUseCase) {
		this.detailPlanCompleteOperationUseCase = detailPlanCompleteOperationUseCase;
	}

	@PatchMapping(value = "/completed")
	public ResponseEntity<DetailPlanView> completeDetailPlan(
		@PathVariable(value = "plan_id") Long plan_id,
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) {
		log.info("\"COMPLETED DETAIL PLAN\"");

		var command = DetailPlanCompleteOperationUseCase.DetailPlanCompleteCommand.builder()
			.planId(plan_id)
			.detailPlanId(detail_plan_id)
			.admin(false)
			.build();
		DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanCompleteOperationUseCase.completeDetailPlan(
			command);

		return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
	}

	@PatchMapping(value = "/completed/admin")
	public ResponseEntity<DetailPlanView> completeDetailPlanAdmin(
		@PathVariable(value = "plan_id") Long plan_id,
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) {
		log.info("\"COMPLETED DETAIL PLAN (admin)\"");

		var command = DetailPlanCompleteOperationUseCase.DetailPlanCompleteCommand.builder()
			.planId(plan_id)
			.detailPlanId(detail_plan_id)
			.admin(true)
			.build();
		DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanCompleteOperationUseCase.completeDetailPlan(
			command);

		return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
	}

	@PatchMapping(value = "/completed-undo")
	public ResponseEntity<DetailPlanView> undoCompleteDetailPlan(
		@PathVariable(value = "plan_id") Long plan_id,
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) {
		log.info("\"COMPLETED UNDO DETAIL PLAN\"");

		var command = DetailPlanCompleteOperationUseCase.DetailPlanCompleteCommand.builder()
			.planId(plan_id)
			.detailPlanId(detail_plan_id)
			.admin(false)
			.build();
		DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanCompleteOperationUseCase.undoCompleteDetailPlan(
			command);

		return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
	}

	@PatchMapping(value = "/completed-undo/admin")
	public ResponseEntity<DetailPlanView> undoCompleteDetailPlanAdmin(
		@PathVariable(value = "plan_id") Long plan_id,
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) {
		log.info("\"COMPLETED UNDO DETAIL PLAN (admin)\"");

		var command = DetailPlanCompleteOperationUseCase.DetailPlanCompleteCommand.builder()
			.planId(plan_id)
			.detailPlanId(detail_plan_id)
			.admin(true)
			.build();
		DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanCompleteOperationUseCase.undoCompleteDetailPlan(
			command);

		return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
	}
}
