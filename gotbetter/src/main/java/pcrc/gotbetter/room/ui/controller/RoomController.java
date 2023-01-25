package pcrc.gotbetter.room.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.room.service.RoomOperationUseCase;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.room.ui.requestBody.RoomCreateRequest;
import pcrc.gotbetter.room.ui.view.RoomView;

@Slf4j
@RestController
@RequestMapping(value = "/rooms")
public class RoomController {
    private final RoomOperationUseCase roomOperationUseCase;

    @Autowired
    public RoomController(RoomOperationUseCase roomOperationUseCase) {
        this.roomOperationUseCase = roomOperationUseCase;
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
}
