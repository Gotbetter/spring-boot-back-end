package pcrc.gotbetter.plan.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.plan.service.PlanOperationUseCase;
import pcrc.gotbetter.plan.service.PlanReadUseCase;
import pcrc.gotbetter.plan.ui.requestBody.PlanCreateRequest;
import pcrc.gotbetter.plan.ui.view.PlanView;

@Slf4j
@RestController
@RequestMapping(value = "/plans")
public class PlanController {
	private final PlanOperationUseCase planOperationUseCase;
	private final PlanReadUseCase planReadUseCase;

	@Autowired
	public PlanController(PlanOperationUseCase planOperationUseCase, PlanReadUseCase planReadUseCase) {
		this.planOperationUseCase = planOperationUseCase;
		this.planReadUseCase = planReadUseCase;
	}

	@PostMapping(value = "")
	public ResponseEntity<List<PlanView>> createPlans(
		@Valid @RequestBody PlanCreateRequest request,
		@RequestParam(name = "admin", required = false) Boolean admin
	) {

		log.info("\"CREATE PLANS\"");

		var command = PlanOperationUseCase.PlanCreateCommand.builder()
			.participantId(request.getParticipant_id())
			.admin(admin != null && admin)
			.build();

		List<PlanView> planViews = new ArrayList<>();
		List<PlanReadUseCase.FindPlanResult> results = planOperationUseCase.createPlans(command);
		for (PlanReadUseCase.FindPlanResult r : results) {
			planViews.add(PlanView.builder().planResult(r).build());
		}

		return ResponseEntity.created(null).body(planViews);
	}

	@GetMapping("/{participant_id}")
	public ResponseEntity<PlanView> getWeekPlan(@PathVariable("participant_id") Long participant_id,
		@RequestParam(value = "week") Integer week) {

		log.info("\"GET A WEEK PLAN\"");

		var query = PlanReadUseCase.PlanFindQuery.builder()
			.participantId(participant_id)
			.week(week)
			.build();

		PlanReadUseCase.FindPlanResult result = planReadUseCase.getWeekPlan(query);

		return ResponseEntity.ok(PlanView.builder().planResult(result).build());
	}

	@GetMapping("/{participant_id}/all")
	public ResponseEntity<List<PlanView>> getAllWeekPlan(@PathVariable("participant_id") Long participantId) {

		log.info("\"GET ALL WEEK PLAN\"");

		List<PlanReadUseCase.FindPlanResult> results = planReadUseCase.getAllWeekPlan(participantId);
		List<PlanView> planViews = new ArrayList<>();

		for (PlanReadUseCase.FindPlanResult r : results) {
			planViews.add(PlanView.builder().planResult(r).build());
		}
		return ResponseEntity.ok(planViews);
	}
}
