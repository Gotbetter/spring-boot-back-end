package pcrc.gotbetter.user_room.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.user_room.service.UserRoomOperationUseCase;
import pcrc.gotbetter.user_room.service.UserRoomReadUseCase;
import pcrc.gotbetter.user_room.ui.requestBody.UserRoomJoinApproveRequest;
import pcrc.gotbetter.user_room.ui.requestBody.UserRoomJoinRequest;
import pcrc.gotbetter.room.ui.view.RoomView;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.service.UserReadUseCase;
import pcrc.gotbetter.user.ui.view.UserView;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/userrooms")
public class UserRoomController {
    private final UserRoomOperationUseCase userRoomOperationUseCase;
    private final UserRoomReadUseCase userRoomReadUseCase;

    @Autowired
    public UserRoomController(UserRoomOperationUseCase userRoomOperationUseCase,
                              UserRoomReadUseCase userRoomReadUseCase) {
        this.userRoomOperationUseCase = userRoomOperationUseCase;
        this.userRoomReadUseCase = userRoomReadUseCase;
    }

    @PostMapping(value = "")
    public ResponseEntity<RoomView> joinTheRoom(@Valid @RequestBody UserRoomJoinRequest request) {

        log.info("\"REQUEST JOIN THE ROOM\"");

        RoomReadUseCase.FindRoomResult result = userRoomOperationUseCase.requestJoinRoom(request.getRoom_code());

        return ResponseEntity.created(null).body(RoomView.builder().roomResult(result).build());
    }

    @GetMapping(value = "/{room_id}")
    public ResponseEntity<List<UserView>> showOneRoom(@PathVariable Long room_id,
                                         @RequestParam(value = "accepted") Boolean accepted) {

        if (accepted == null) {
            throw new GotBetterException(MessageType.BAD_REQUEST);
        } else if (accepted) {
            log.info("\"ROOM MEMBER LIST\"");
        } else {
            log.info("\"WAIT LIST FOR APPROVE\"");
        }

        List<UserView> userViews = new ArrayList<>();
        List<UserReadUseCase.FindUserResult> result = userRoomReadUseCase.getMemberListInARoom(room_id, accepted);
        for (UserReadUseCase.FindUserResult r : result) {
            userViews.add(UserView.builder().userResult(r).build());
        }

        return ResponseEntity.ok(userViews);
    }

    @PatchMapping(value = "")
    public ResponseEntity<UserView> approveJoinRoom(@Valid @RequestBody UserRoomJoinApproveRequest request) {

        log.info("\"APPROVE JOIN ROOM\"");

        var command = UserRoomOperationUseCase.UserRoomAcceptedUpdateCommand.builder()
                .id(request.getId())
                .room_id(request.getRoom_id())
                .build();
        UserReadUseCase.FindUserResult result = userRoomOperationUseCase.approveJoinRoom(command);

        return ResponseEntity.ok(UserView.builder().userResult(result).build());
    }
}
