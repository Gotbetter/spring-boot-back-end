package pcrc.gotbetter.detail_plan_evaluation.ui.controller;

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
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalOperationUseCase;
import pcrc.gotbetter.detail_plan_evaluation.service.DetailPlanEvalReadUseCase;
import pcrc.gotbetter.detail_plan_evaluation.ui.request_body.DetailDislikeRequest;
import pcrc.gotbetter.detail_plan_evaluation.ui.view.DetailDislikeView;
import pcrc.gotbetter.detail_plan_evaluation.ui.view.DetailPlanEvaluationView;

@Slf4j
@RestController
@RequestMapping(value = "/details/{detail_plan_id}/dislike")
public class DetailPlanEvalController {
	private final DetailPlanEvalOperationUseCase detailPlanEvalOperationUseCase;
	private final DetailPlanEvalReadUseCase detailPlanEvalReadUseCase;

	@Autowired
	public DetailPlanEvalController(DetailPlanEvalOperationUseCase detailPlanEvalOperationUseCase,
		DetailPlanEvalReadUseCase detailPlanEvalReadUseCase) {
		this.detailPlanEvalOperationUseCase = detailPlanEvalOperationUseCase;
		this.detailPlanEvalReadUseCase = detailPlanEvalReadUseCase;
	}

	@PostMapping(value = "")
	public ResponseEntity<DetailPlanEvaluationView> createDetailPlanEvaluation(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) {

		log.info("\"CREATE A DETAIL PLAN DISLIKE\"");

		var command = DetailPlanEvalOperationUseCase.DetailPlanEvaluationCommand.builder()
			.detailPlanId(detail_plan_id)
			.admin(false)
			.build();
		DetailPlanEvalReadUseCase.FindDetailPlanEvalResult result = detailPlanEvalOperationUseCase.createDetailPlanEvaluation(
			command);
		return ResponseEntity.created(null)
			.body(DetailPlanEvaluationView.builder().detailPlanEvalResult(result).build());
	}

	@PostMapping(value = "/admin")
	public ResponseEntity<DetailPlanEvaluationView> createDetailPlanEvaluationAdmin(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@Valid @RequestBody DetailDislikeRequest request
	) {

		log.info("\"CREATE A DETAIL PLAN DISLIKE (admin)\"");

		var command = DetailPlanEvalOperationUseCase.DetailPlanEvaluationCommand.builder()
			.detailPlanId(detail_plan_id)
			.userId(request.getUser_id())
			.admin(true)
			.build();
		DetailPlanEvalReadUseCase.FindDetailPlanEvalResult result = detailPlanEvalOperationUseCase.createDetailPlanEvaluation(
			command);
		return ResponseEntity.created(null)
			.body(DetailPlanEvaluationView.builder().detailPlanEvalResult(result).build());
	}

	@DeleteMapping(value = "")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<DetailPlanEvaluationView> deleteDetailPlanEvaluation(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) {

		log.info("\"DELETE A DETAIL PLAN DISLIKE\"");

		var command = DetailPlanEvalOperationUseCase.DetailDislikeDeleteCommand.builder()
			.detailPlanId(detail_plan_id)
			.admin(false)
			.build();
		DetailPlanEvalReadUseCase.FindDetailPlanEvalResult result = detailPlanEvalOperationUseCase.deleteDetailPlanEvaluation(
			command);
		return ResponseEntity.created(null)
			.body(DetailPlanEvaluationView.builder().detailPlanEvalResult(result).build());
	}

	@DeleteMapping(value = "/{participant_id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<DetailPlanEvaluationView> deleteDetailPlanEvaluationAdmin(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id,
		@PathVariable(value = "participant_id") Long participant_id
	) {

		log.info("\"DELETE A DETAIL PLAN DISLIKE (admin)\"");

		var command = DetailPlanEvalOperationUseCase.DetailDislikeDeleteCommand.builder()
			.detailPlanId(detail_plan_id)
			.participantId(participant_id)
			.admin(true)
			.build();
		DetailPlanEvalReadUseCase.FindDetailPlanEvalResult result = detailPlanEvalOperationUseCase.deleteDetailPlanEvaluation(
			command);
		return ResponseEntity.created(null)
			.body(DetailPlanEvaluationView.builder().detailPlanEvalResult(result).build());
	}

	@GetMapping(value = "/admin")
	public ResponseEntity<List<DetailDislikeView>> getDetailDislikeList(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) throws IOException {

		log.info("\"GET A DETAIL DISLIKE LIST (admin)\"");

		var query = DetailPlanEvalReadUseCase.DetailDislikeFindQuery.builder()
			.detailPlanId(detail_plan_id)
			.build();
		List<DetailPlanEvalReadUseCase.FindDetailDislikeListResult> results = detailPlanEvalReadUseCase.getDetailDislikeList(
			query);
		List<DetailDislikeView> detailDislikeViews = new ArrayList<>();

		for (DetailPlanEvalReadUseCase.FindDetailDislikeListResult result : results) {
			detailDislikeViews.add(DetailDislikeView.builder().detailDislikeListResult(result).build());
		}
		return ResponseEntity.ok(detailDislikeViews);
	}

	@GetMapping(value = "/members")
	public ResponseEntity<List<DetailDislikeView>> getDetailNotDislikeList(
		@PathVariable(value = "detail_plan_id") Long detail_plan_id
	) throws IOException {

		log.info("\"GET A DETAIL NOT DISLIKE LIST (admin)\"");

		var query = DetailPlanEvalReadUseCase.DetailDislikeFindQuery.builder()
			.detailPlanId(detail_plan_id)
			.build();
		List<DetailPlanEvalReadUseCase.FindDetailDislikeListResult> results = detailPlanEvalReadUseCase.getDetailNotDislikeList(
			query);
		List<DetailDislikeView> detailDislikeViews = new ArrayList<>();

		for (DetailPlanEvalReadUseCase.FindDetailDislikeListResult result : results) {
			detailDislikeViews.add(DetailDislikeView.builder().detailDislikeListResult(result).build());
		}

		return ResponseEntity.ok(detailDislikeViews);
	}
}
