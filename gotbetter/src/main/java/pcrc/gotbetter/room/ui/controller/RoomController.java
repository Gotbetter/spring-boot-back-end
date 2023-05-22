package pcrc.gotbetter.room.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.room.service.RoomOperationUseCase;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.room.ui.requestBody.RoomCreateRequest;
import pcrc.gotbetter.room.ui.view.RankView;
import pcrc.gotbetter.room.ui.view.RoomView;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping(value = "")
    public ResponseEntity<List<RoomView>> showIncludedRooms() {

        log.info("\"GET USER'S ROOMS\"");

        List<RoomReadUseCase.FindRoomResult> result = roomReadUseCase.getUserRooms();

        List<RoomView> roomViews = new ArrayList<>();
        for (RoomReadUseCase.FindRoomResult r : result) {
            roomViews.add(RoomView.builder().roomResult(r).build());
        }
        return ResponseEntity.ok(roomViews);
    }

    @GetMapping(value = "/{room_id}")
    public ResponseEntity<RoomView> showOneRoom(@PathVariable Long room_id) {

        log.info("\"GET A ROOM INFO\"");

        RoomReadUseCase.FindRoomResult result = roomReadUseCase.getOneRoomInfo(room_id);

        return ResponseEntity.ok(RoomView.builder().roomResult(result).build());
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
                .ruleId(request.getRule_id())
                .account(request.getAccount())
                .build();
        RoomReadUseCase.FindRoomResult result = roomOperationUseCase.createRoom(command);

        return ResponseEntity.created(null).body(RoomView.builder().roomResult(result).build());
    }

    @GetMapping(value = "/{room_id}/rank")
    public ResponseEntity<List<RankView>> getRank(@PathVariable Long room_id) {

        log.info("\"GET A RANK LIST\"");

        List<RoomReadUseCase.FindRankResult> result = roomReadUseCase.getRank(room_id);

        List<RankView> rankViewList = new ArrayList<>();
        for (RoomReadUseCase.FindRankResult rank : result) {
            rankViewList.add(RankView.builder().rankResult(rank).build());
        }
        return ResponseEntity.ok(rankViewList);
    }
}
