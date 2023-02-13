package pcrc.gotbetter.plan.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.plan.service.PlanOperationUseCase;
import pcrc.gotbetter.plan.service.PlanReadUseCase;
import pcrc.gotbetter.plan.ui.requestBody.PlanCreateRequest;
import pcrc.gotbetter.plan.ui.view.PlanView;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<List<PlanView>> createPlans(@Valid @RequestBody PlanCreateRequest request) {

        log.info("\"CREATE PLANS\"");

        var command = PlanOperationUseCase.PlanCreateCommand.builder()
                .participant_id(request.getParticipant_id())
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
                .participant_id(participant_id)
                .week(week)
                .build();

        PlanReadUseCase.FindPlanResult result = planReadUseCase.getWeekPlan(query);

        return ResponseEntity.ok(PlanView.builder().planResult(result).build());
    }
}
