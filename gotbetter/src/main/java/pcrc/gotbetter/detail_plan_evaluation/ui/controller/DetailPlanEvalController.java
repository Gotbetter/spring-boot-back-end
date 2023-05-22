package pcrc.gotbetter.detail_plan_evaluation.ui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalOperationUseCase;
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalReadUseCase;
import pcrc.gotbetter.detail_plan_evaluation.ui.view.DetailPlanEvaluationView;

@Slf4j
@RestController
@RequestMapping(value = "/details/{detail_plan_id}/dislike")
public class DetailPlanEvalController {
    private final DetailPlanEvalOperationUseCase detailPlanEvalOperationUseCase;

    @Autowired
    public DetailPlanEvalController(DetailPlanEvalOperationUseCase detailPlanEvalOperationUseCase) {
        this.detailPlanEvalOperationUseCase = detailPlanEvalOperationUseCase;
    }

    @PostMapping(value = "")
    public ResponseEntity<DetailPlanEvaluationView> createDetailPlanEvaluation(@PathVariable(value = "detail_plan_id") Long detail_plan_id) throws InterruptedException {

        log.info("\"CREATE A DETAIL PLAN DISLIKE\"");

        var command = DetailPlanEvalOperationUseCase.DetailPlanEvaluationCommand.builder()
                .detailPlanId(detail_plan_id)
                .build();
        DetailPlanEvalReadUseCase.FindDetailPlanEvalResult result = detailPlanEvalOperationUseCase.createDetailPlanEvaluation(command);
        return ResponseEntity.created(null).body(DetailPlanEvaluationView.builder().detailPlanEvalResult(result).build());
    }

    @DeleteMapping(value = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<DetailPlanEvaluationView> deleteDetailPlanEvaluation(@PathVariable(value = "detail_plan_id") Long detail_plan_id) {

        log.info("\"DELETE A DETAIL PLAN DISLIKE\"");

        var command = DetailPlanEvalOperationUseCase.DetailPlanEvaluationCommand.builder()
                .detailPlanId(detail_plan_id)
                .build();
        DetailPlanEvalReadUseCase.FindDetailPlanEvalResult result = detailPlanEvalOperationUseCase.deleteDetailPlanEvaluation(command);
        return ResponseEntity.created(null).body(DetailPlanEvaluationView.builder().detailPlanEvalResult(result).build());

    }
}
