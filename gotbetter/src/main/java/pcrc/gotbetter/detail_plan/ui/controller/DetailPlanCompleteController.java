package pcrc.gotbetter.detail_plan.ui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<DetailPlanView> completeDetailPlan(@PathVariable(value = "plan_id") Long plan_id,
                                                             @PathVariable(value = "detail_plan_id") Long detail_plan_id) {
        log.info("COMPLETED DETAIL PLAN");

        var command = DetailPlanCompleteOperationUseCase.DetailPlanCompleteCommand.builder()
                .planId(plan_id)
                .detailPlanId(detail_plan_id)
                .build();
        DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanCompleteOperationUseCase.completeDetailPlan(command);

        return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
    }

    @PatchMapping(value = "/completed-undo")
    public ResponseEntity<DetailPlanView> undoCompleteDetailPlan(@PathVariable(value = "plan_id") Long plan_id,
                                                             @PathVariable(value = "detail_plan_id") Long detail_plan_id) {
        log.info("COMPLETED DETAIL PLAN");

        var command = DetailPlanCompleteOperationUseCase.DetailPlanCompleteCommand.builder()
                .planId(plan_id)
                .detailPlanId(detail_plan_id)
                .build();
        DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanCompleteOperationUseCase.undoCompleteDetailPlan(command);

        return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
    }
}
