package pcrc.gotbetter.room.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.room.service.RoomOperationUseCase;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.room.ui.requestBody.RoomCreateRequest;
import pcrc.gotbetter.room.ui.requestBody.RoomUpdateRequest;
import pcrc.gotbetter.room.ui.view.RankView;
import pcrc.gotbetter.room.ui.view.RoomView;

@Slf4j
@RestController
@RequestMapping(value = "/rooms")
public class RoomController {
	private final RoomOperationUseCase roomOperationUseCase;
	private final RoomReadUseCase roomReadUseCase;

	@Autowired
	public RoomController(RoomOperationUseCase roomOperationUseCase, RoomReadUseCase roomReadUseCase) {
		this.roomOperationUseCase = roomOperationUseCase;
		this.roomReadUseCase = roomReadUseCase;
	}

	@PostMapping(value = "")
	public ResponseEntity<RoomView> createNewRoom(@Valid @RequestBody RoomCreateRequest request) {

		log.info("\"CREATE A ROOM\"");

		var command = RoomOperationUseCase.RoomCreateCommand.builder()
			.title(request.getTitle())
			.maxUserNum(request.getMax_user_num())
			.startDate(request.getStart_date())
			.week(request.getWeek())
			.currentWeek(request.getCurrent_week())
			.entryFee(request.getEntry_fee())
			.ruleCode(request.getRule_code())
			.account(request.getAccount())
			.roomCategoryCode(request.getRoom_category_code())
			.description(request.getDescription())
			.build();
		RoomReadUseCase.FindRoomResult result = roomOperationUseCase.createRoom(command);

		return ResponseEntity.created(null).body(RoomView.builder().roomResult(result).build());
	}

	@GetMapping(value = "")
	public ResponseEntity<List<RoomView>> showIncludedRooms(
		@RequestParam(name = "admin", required = false) Boolean admin
	) {

		log.info("\"GET USER'S ROOMS\"");

		List<RoomReadUseCase.FindRoomResult> result = roomReadUseCase.getUserRoomList(admin != null && admin);

		List<RoomView> roomViews = new ArrayList<>();
		for (RoomReadUseCase.FindRoomResult r : result) {
			roomViews.add(RoomView.builder().roomResult(r).build());
		}
		return ResponseEntity.ok(roomViews);
	}

	@GetMapping(value = "/{room_id}")
	public ResponseEntity<RoomView> showOneRoom(
		@PathVariable Long room_id,
		@RequestParam(name = "admin", required = false) Boolean admin
	) {

		log.info("\"GET A ROOM INFO\"");

		var query = RoomReadUseCase.RoomFindQuery.builder()
			.roomId(room_id)
			.admin(admin != null && admin)
			.build();
		RoomReadUseCase.FindRoomResult result = roomReadUseCase.getOneRoomInfo(query);

		return ResponseEntity.ok(RoomView.builder().roomResult(result).build());
	}

	@PatchMapping(value = "/{room_id}")
	public ResponseEntity<RoomView> modifyDescription(@PathVariable Long room_id,
		@Valid @RequestBody RoomUpdateRequest roomUpdateRequest) {

		log.info("\"MODIFY ROOM DESCRIPTION\"");

		var command = RoomOperationUseCase.RoomUpdateCommand.builder()
			.room_id(room_id)
			.description(roomUpdateRequest.getDescription())
			.build();
		RoomReadUseCase.FindRoomResult result = roomOperationUseCase.updateRoom(command);

		return ResponseEntity.ok(RoomView.builder().roomResult(result).build());
	}

	@GetMapping(value = "/{room_id}/rank")
	public ResponseEntity<List<RankView>> getRank(@PathVariable Long room_id) throws IOException {

		log.info("\"GET A RANK LIST\"");

		List<RoomReadUseCase.FindRankResult> result = roomReadUseCase.getRank(room_id);
		List<RankView> rankViewList = new ArrayList<>();

		for (RoomReadUseCase.FindRankResult rank : result) {
			rankViewList.add(RankView.builder().rankResult(rank).build());
		}
		return ResponseEntity.ok(rankViewList);
	}
}
