package pcrc.gotbetter.common.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.common.service.CommonCodeReadUseCase;
import pcrc.gotbetter.common.ui.view.RoomCategoryView;
import pcrc.gotbetter.common.ui.view.RuleView;

@Slf4j
@RestController
@RequestMapping(value = "/common")
public class CommonCodeController {
	private final CommonCodeReadUseCase commonCodeReadUseCase;

	@Autowired
	public CommonCodeController(CommonCodeReadUseCase commonCodeReadUseCase) {
		this.commonCodeReadUseCase = commonCodeReadUseCase;
	}

	@GetMapping(value = "/room-categories")
	public ResponseEntity<List<RoomCategoryView>> roomCategoryList() throws IOException {
		log.info("\"GET ROOM CATEGORIES\"");

		List<CommonCodeReadUseCase.FindCommonCodeResult> result = commonCodeReadUseCase.getRoomCategories();
		List<RoomCategoryView> roomCategoryViews = new ArrayList<>();

		for (CommonCodeReadUseCase.FindCommonCodeResult r : result) {
			roomCategoryViews.add(RoomCategoryView.builder().commonCodeResult(r).build());
		}
		return ResponseEntity.ok(roomCategoryViews);
	}

	@GetMapping(value = "/rules")
	public ResponseEntity<List<RuleView>> ruleList() {
		log.info("\"GET ROOM RULES\"");

		List<CommonCodeReadUseCase.FindCommonCodeResult> result = commonCodeReadUseCase.getRules();
		List<RuleView> ruleViews = new ArrayList<>();

		for (CommonCodeReadUseCase.FindCommonCodeResult r : result) {
			ruleViews.add(RuleView.builder().commonCodeResult(r).build());
		}
		return ResponseEntity.ok(ruleViews);
	}
}
