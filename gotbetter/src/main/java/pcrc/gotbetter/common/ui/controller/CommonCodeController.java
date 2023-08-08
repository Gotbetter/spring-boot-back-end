package pcrc.gotbetter.common.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.common.service.CommonCodeOperationUseCase;
import pcrc.gotbetter.common.service.CommonCodeReadUseCase;
import pcrc.gotbetter.common.ui.request_body.CommonUpdateRequest;
import pcrc.gotbetter.common.ui.view.RoomCategoryView;
import pcrc.gotbetter.common.ui.view.RuleView;

@Slf4j
@RestController
@RequestMapping(value = "/common")
public class CommonCodeController {
	private final CommonCodeReadUseCase commonCodeReadUseCase;
	private final CommonCodeOperationUseCase commonCodeOperationUseCase;

	@Autowired
	public CommonCodeController(CommonCodeReadUseCase commonCodeReadUseCase,
		CommonCodeOperationUseCase commonCodeOperationUseCase) {
		this.commonCodeReadUseCase = commonCodeReadUseCase;
		this.commonCodeOperationUseCase = commonCodeOperationUseCase;
	}

	@GetMapping(value = "/room-categories")
	public ResponseEntity<List<RoomCategoryView>> roomCategoryList(
		@RequestParam(name = "admin", required = false) Boolean admin
	) throws IOException {
		log.info("\"GET ROOM CATEGORIES\"");

		List<CommonCodeReadUseCase.FindCommonCodeResult> result = commonCodeReadUseCase.getRoomCategories(
			admin != null && admin);
		List<RoomCategoryView> roomCategoryViews = new ArrayList<>();

		for (CommonCodeReadUseCase.FindCommonCodeResult r : result) {
			roomCategoryViews.add(RoomCategoryView.builder().commonCodeResult(r).build());
		}
		return ResponseEntity.ok(roomCategoryViews);
	}

	@GetMapping(value = "/rules")
	public ResponseEntity<List<RuleView>> ruleList(@RequestParam(name = "admin", required = false) Boolean admin) {
		log.info("\"GET ROOM RULES\"");

		List<CommonCodeReadUseCase.FindCommonCodeResult> result = commonCodeReadUseCase.getRules(
			admin != null && admin);
		List<RuleView> ruleViews = new ArrayList<>();

		for (CommonCodeReadUseCase.FindCommonCodeResult r : result) {
			ruleViews.add(RuleView.builder().commonCodeResult(r).build());
		}
		return ResponseEntity.ok(ruleViews);
	}

	@PatchMapping(value = "")
	public void updateCommonInfo(@Valid @RequestBody CommonUpdateRequest request) {
		log.info("\"UPDATE COMMON INFO\"");

		var command = CommonCodeOperationUseCase.CommonCodeUpdateCommand.builder()
			.groupCode(request.getGroup_code())
			.code(request.getCode())
			.codeDescription(request.getCode_description())
			.attribute1(request.getAttribute1())
			.attribute2(request.getAttribute2())
			.build();
		commonCodeOperationUseCase.updateCommonInfo(command);
	}

	@PostMapping(value = "")
	public void createCommonInfo(@Valid @RequestBody CommonUpdateRequest request) {
		log.info("\"CREATE COMMON INFO\"");

		var command = CommonCodeOperationUseCase.CommonCodeUpdateCommand.builder()
			.groupCode(request.getGroup_code())
			.code(request.getCode())
			.codeDescription(request.getCode_description())
			.attribute1(request.getAttribute1())
			.attribute2(request.getAttribute2())
			.build();
		commonCodeOperationUseCase.createCommonInfo(command);

		// return ResponseEntity.created(null).body(UserView.builder().userResult(result).build());
	}

	// @DeleteMapping(value = "")
	// public void deleteCommonInfo(@RequestParam(name = "groupCode") String groupCode,
	// 	@RequestParam(name = "code") String code) {
	// 	log.info("\"DELETE COMMON INFO\"");
	//
	// 	var command = CommonCodeOperationUseCase.CommonCodeDeleteCommand.builder()
	// 		.groupCode(groupCode)
	// 		.code(code)
	// 		.build();
	// 	// commonCodeOperationUseCase.createCommonInfo(command);
	//
	// 	// return ResponseEntity.created(null).body(UserView.builder().userResult(result).build());
	// }
}
