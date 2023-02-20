package pcrc.gotbetter.detail_plan_evaluation.ui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalOperationUseCase;

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
    @ResponseStatus(HttpStatus.CREATED)
    public void createDetailPlanEvaluation(@PathVariable(value = "detail_plan_id") Long detail_plan_id) {
        log.info("CREATE A DETAIL PLAN DISLIKE");

        var command = DetailPlanEvalOperationUseCase.DetailPlanEvaluationCommand.builder()
                .detail_plan_id(detail_plan_id)
                .build();
        detailPlanEvalOperationUseCase.createDetailPlanEvaluation(command);
    }

    @DeleteMapping(value = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDetailPlanEvaluation(@PathVariable(value = "detail_plan_id") Long detail_plan_id) {
        log.info("DELETE A DETAIL PLAN DISLIKE");

        var command = DetailPlanEvalOperationUseCase.DetailPlanEvaluationCommand.builder()
                .detail_plan_id(detail_plan_id)
                .build();
        detailPlanEvalOperationUseCase.deleteDetailPlanEvaluation(command);
    }
}
