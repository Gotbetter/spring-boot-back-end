package pcrc.gotbetter.plan_evaluation.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.plan_evaluation.service.PlanEvaluationOperationUseCase;
import pcrc.gotbetter.plan_evaluation.service.PlanEvaluationReadUseCase;
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

	@GetMapping(value = "")
	public ResponseEntity<PlanEvaluationView> getPlanDislike(@PathVariable(value = "plan_id") Long plan_id) {

		log.info("\"GET A PLAN DISLIKE LIST\"");

		var query = PlanEvaluationReadUseCase.PlanEvaluationFindQuery.builder()
			.planId(plan_id)
			.build();
		PlanEvaluationReadUseCase.FindPlanEvaluationResult result = planEvaluationReadUseCase.getPlanDislike(query);

		return ResponseEntity.ok(PlanEvaluationView.builder().planEvaluationResult(result).build());
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
}
