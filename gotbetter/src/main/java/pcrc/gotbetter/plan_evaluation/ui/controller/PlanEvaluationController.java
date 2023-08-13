package pcrc.gotbetter.plan_evaluation.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.plan_evaluation.service.PlanEvaluationOperationUseCase;
import pcrc.gotbetter.plan_evaluation.service.PlanEvaluationReadUseCase;
import pcrc.gotbetter.plan_evaluation.ui.request_body.PlanDislikeRequest;
import pcrc.gotbetter.plan_evaluation.ui.view.PlanDislikeListView;
import pcrc.gotbetter.plan_evaluation.ui.view.PlanEvaluationView;

@Slf4j
@RestController
@RequestMapping(value = "/plans/{plan_id}/dislike")
public class PlanEvaluationController {
	private final PlanEvaluationOperationUseCase planEvaluationOperationUseCase;
	private final PlanEvaluationReadUseCase planEvaluationReadUseCase;

	@Autowired
	public PlanEvaluationController(
		PlanEvaluationOperationUseCase planEvaluationOperationUseCase,
		PlanEvaluationReadUseCase planEvaluationReadUseCase
	) {
		this.planEvaluationOperationUseCase = planEvaluationOperationUseCase;
		this.planEvaluationReadUseCase = planEvaluationReadUseCase;
	}

	@PostMapping(value = "")
	public ResponseEntity<PlanEvaluationView> createPlanDislike(@PathVariable(value = "plan_id") Long plan_id) {

		log.info("\"CREATE A PLAN DISLIKE\"");

		var command = PlanEvaluationOperationUseCase.PlanEvaluationCommand.builder()
			.planId(plan_id)
			.build();
		PlanEvaluationReadUseCase.FindPlanEvaluationResult result = planEvaluationOperationUseCase.createPlanEvaluation(
			command);
		return ResponseEntity.created(null).body(PlanEvaluationView.builder().planEvaluationResult(result).build());
	}

	@PostMapping(value = "/admin")
	public ResponseEntity<PlanEvaluationView> createPlanDislikeAdmin(
		@PathVariable(value = "plan_id") Long plan_id,
		@Valid @RequestBody PlanDislikeRequest request
	) {

		log.info("\"CREATE A PLAN DISLIKE (admin)\"");

		var command = PlanEvaluationOperationUseCase.PlanEvaluationAdminCommand.builder()
			.planId(plan_id)
			.userId(request.getUser_id())
			.build();
		PlanEvaluationReadUseCase.FindPlanEvaluationResult result = planEvaluationOperationUseCase.createPlanEvaluationAdmin(
			command);

		return ResponseEntity.created(null).body(PlanEvaluationView.builder().planEvaluationResult(result).build());
	}

	@GetMapping(value = "")
	public ResponseEntity<PlanEvaluationView> getPlanDislike(@PathVariable(value = "plan_id") Long plan_id) {

		log.info("\"GET A PLAN DISLIKE LIST\"");

		var query = PlanEvaluationReadUseCase.PlanEvaluationFindQuery.builder()
			.planId(plan_id)
			.build();
		PlanEvaluationReadUseCase.FindPlanEvaluationResult result = planEvaluationReadUseCase.getPlanDislike(query);

		return ResponseEntity.ok(PlanEvaluationView.builder().planEvaluationResult(result).build());
	}

	@GetMapping(value = "/admin")
	public ResponseEntity<List<PlanDislikeListView>> getPlanDislikeList(
		@PathVariable(value = "plan_id") Long planId
	) throws IOException {

		log.info("\"GET A PLAN DISLIKE LIST (admin)\"");

		var query = PlanEvaluationReadUseCase.PlanEvaluationFindQuery.builder()
			.planId(planId)
			.build();
		List<PlanEvaluationReadUseCase.FindPlanDislikeListResult> results = planEvaluationReadUseCase.getPlanDislikeList(
			query);
		List<PlanDislikeListView> planDislikeListViews = new ArrayList<>();

		for (PlanEvaluationReadUseCase.FindPlanDislikeListResult result : results) {
			planDislikeListViews.add(PlanDislikeListView.builder().planDislikeListResult(result).build());
		}

		return ResponseEntity.ok(planDislikeListViews);
	}

	@GetMapping(value = "/members")
	public ResponseEntity<List<PlanDislikeListView>> getPlanNotDislikeList(
		@PathVariable(value = "plan_id") Long planId
	) throws IOException {

		log.info("\"GET A PLAN NOT DISLIKE LIST (admin)\"");

		var query = PlanEvaluationReadUseCase.PlanEvaluationFindQuery.builder()
			.planId(planId)
			.build();
		List<PlanEvaluationReadUseCase.FindPlanDislikeListResult> results = planEvaluationReadUseCase.getPlanNotDislikeList(
			query);
		List<PlanDislikeListView> planDislikeListViews = new ArrayList<>();

		for (PlanEvaluationReadUseCase.FindPlanDislikeListResult result : results) {
			planDislikeListViews.add(PlanDislikeListView.builder().planDislikeListResult(result).build());
		}

		return ResponseEntity.ok(planDislikeListViews);
	}

	@DeleteMapping(value = "")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePlanDislike(@PathVariable(value = "plan_id") Long plan_id) {

		log.info("\"DELETE A PLAN DISLIKE\"");

		var command = PlanEvaluationOperationUseCase.PlanEvaluationCommand.builder()
			.planId(plan_id)
			.build();
		planEvaluationOperationUseCase.deletePlanEvaluation(command);
	}

	@DeleteMapping(value = "/{participant_id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePlanDislikeAdmin(
		@PathVariable(value = "plan_id") Long plan_id,
		@PathVariable(value = "participant_id") Long participant_id
	) {

		log.info("\"DELETE A PLAN DISLIKE (admin)\"");

		var command =
			PlanEvaluationOperationUseCase.PlanEvaluationDeleteAdminCommand.builder()
				.planId(plan_id)
				.participantId(participant_id)
				.build();
		planEvaluationOperationUseCase.deletePlanEvaluationAdmin(command);
	}
}
