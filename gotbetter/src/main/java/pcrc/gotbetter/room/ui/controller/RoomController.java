package pcrc.gotbetter.room.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.room.service.RoomOperationUseCase;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.room.ui.requestBody.RoomCreateRequest;
import pcrc.gotbetter.room.ui.requestBody.RoomJoinRequest;
import pcrc.gotbetter.room.ui.view.RoomView;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.service.UserReadUseCase;
import pcrc.gotbetter.user.ui.view.UserView;

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
    public ResponseEntity<?> showOneRoom(@PathVariable Long room_id,
                                         @RequestParam(value = "accepted", required = false) Boolean accepted) {

        if (accepted != null) {
            return waitListForApprove(room_id, accepted);
        }

        log.info("\"GET A ROOM INFO\"");

        RoomReadUseCase.FindRoomResult result = roomReadUseCase.getOneRoomInfo(room_id);

        return ResponseEntity.ok(RoomView.builder().roomResult(result).build());
    }

    @PostMapping(value = "")
    public ResponseEntity<RoomView> createNewRoom(@Valid @RequestBody RoomCreateRequest request) {

        log.info("\"CREATE A ROOM\"");

        var command = RoomOperationUseCase.RoomCreateCommand.builder()
                .title(request.getTitle())
                .max_user_num(request.getMax_user_num())
                .start_date(request.getStart_date())
                .target_date(request.getTarget_date())
                .entry_fee(request.getEntry_fee())
                .rule_id(request.getRule_id())
                .account(request.getAccount())
                .build();
        RoomReadUseCase.FindRoomResult result = roomOperationUseCase.createRoom(command);

        return ResponseEntity.created(null).body(RoomView.builder().roomResult(result).build());
    }

    @PostMapping(value = "/join")
    public ResponseEntity<RoomView> joinTheRoom(@Valid @RequestBody RoomJoinRequest request) {

        log.info("\"JOIN THE ROOM\"");

        RoomReadUseCase.FindRoomResult result = roomOperationUseCase.requestJoinRoom(request.getRoom_code());

        return ResponseEntity.created(null).body(RoomView.builder().roomResult(result).build());
    }

    private ResponseEntity<List<UserView>> waitListForApprove(Long room_id, Boolean accepted) {

        log.info("\"WAIT LIST FOR APPROVE\"");

        if (accepted) {
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }

        List<UserView> userViews = new ArrayList<>();
        List<UserReadUseCase.FindUserResult> result = roomReadUseCase.getWaitListForApprove(room_id);
        for (UserReadUseCase.FindUserResult r : result) {
            userViews.add(UserView.builder().userResult(r).build());
        }

        return ResponseEntity.ok(userViews);
    }
}
