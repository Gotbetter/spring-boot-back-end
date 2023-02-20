package pcrc.gotbetter.detail_plan.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.detail_plan.service.DetailPlanOperationUseCase;
import pcrc.gotbetter.detail_plan.service.DetailPlanReadUseCase;
import pcrc.gotbetter.detail_plan.ui.requestBody.DetailPlanRequest;
import pcrc.gotbetter.detail_plan.ui.view.DetailPlanView;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/plans/{plan_id}/details")
public class DetailPlanController {
    private final DetailPlanOperationUseCase detailPlanOperationUseCase;
    private final DetailPlanReadUseCase detailPlanReadUseCase;

    @Autowired
    public DetailPlanController(DetailPlanOperationUseCase detailPlanOperationUseCase,
                                DetailPlanReadUseCase detailPlanReadUseCase) {
        this.detailPlanOperationUseCase = detailPlanOperationUseCase;
        this.detailPlanReadUseCase = detailPlanReadUseCase;
    }

    @PostMapping(value = "")
    public ResponseEntity<DetailPlanView> createDetailPlan(@PathVariable(value = "plan_id") Long plan_id,
                                                           @Valid @RequestBody DetailPlanRequest request) {

        log.info("CREATE A DETAIL PLAN");

        var command = DetailPlanOperationUseCase.DetailPlanCreateCommand.builder()
                .plan_id(plan_id)
                .content(request.getContent())
                .build();
        DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanOperationUseCase.createDetailPlan(command);

        return ResponseEntity.created(null).body(DetailPlanView.builder().detailPlanResult(result).build());
    }

    @GetMapping(value = "")
    public ResponseEntity<List<DetailPlanView>> getDetailPlan(@PathVariable(value = "plan_id") Long plan_id) {

        log.info("GET DETAIL PLAN LIST");

        List<DetailPlanReadUseCase.FindDetailPlanResult> detailPlans = detailPlanReadUseCase.getDetailPlans(plan_id);
        List<DetailPlanView> detailPlanViews = new ArrayList<>();

        for (DetailPlanReadUseCase.FindDetailPlanResult d : detailPlans) {
            detailPlanViews.add(DetailPlanView.builder().detailPlanResult(d).build());
        }
        return ResponseEntity.ok(detailPlanViews);
    }

    @PatchMapping(value = "/{detail_plan_id}")
    public ResponseEntity<DetailPlanView> updateDetailPlan(@PathVariable(value = "plan_id") Long plan_id,
                                                           @PathVariable(value = "detail_plan_id") Long detail_plan_id,
                                                           @Valid @RequestBody DetailPlanRequest request) {
        log.info("UPDATE A DETAIL PLAN");

        var command = DetailPlanOperationUseCase.DetailPlanUpdateCommand.builder()
                .detail_plan_id(detail_plan_id)
                .plan_id(plan_id)
                .content(request.getContent())
                .build();
        DetailPlanReadUseCase.FindDetailPlanResult result = detailPlanOperationUseCase.updateDetailPlan(command);
        return ResponseEntity.ok(DetailPlanView.builder().detailPlanResult(result).build());
    }

    @DeleteMapping(value = "/{detail_plan_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDetailPlan(@PathVariable(value = "plan_id") Long plan_id,
                                           @PathVariable(value = "detail_plan_id") Long detail_plan_id) {
        log.info("DELETE A DETAIL PLAN");

        var command = DetailPlanOperationUseCase.DetailPlanDeleteCommand.builder()
                .detail_plan_id(detail_plan_id)
                .plan_id(plan_id)
                .build();
        detailPlanOperationUseCase.deleteDetailPlan(command);
    }
}
