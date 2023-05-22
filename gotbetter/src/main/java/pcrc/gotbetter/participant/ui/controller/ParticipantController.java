package pcrc.gotbetter.participant.ui.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pcrc.gotbetter.participant.ui.view.ParticipantView;
import pcrc.gotbetter.participant.ui.view.RefundView;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.participant.service.ParticipantOperationUseCase;
import pcrc.gotbetter.participant.service.ParticipantReadUseCase;
import pcrc.gotbetter.participant.ui.requestBody.ParticipantJoinApproveRequest;
import pcrc.gotbetter.participant.ui.requestBody.ParticipantJoinRequest;
import pcrc.gotbetter.room.ui.view.RoomView;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/participants")
public class ParticipantController {
    private final ParticipantOperationUseCase participantOperationUseCase;
    private final ParticipantReadUseCase participantReadUseCase;

    @Autowired
    public ParticipantController(ParticipantOperationUseCase participantOperationUseCase,
                                 ParticipantReadUseCase participantReadUseCase) {
        this.participantOperationUseCase = participantOperationUseCase;
        this.participantReadUseCase = participantReadUseCase;
    }

    @PostMapping(value = "")
    public ResponseEntity<RoomView> joinTheRoom(@Valid @RequestBody ParticipantJoinRequest request) {

        log.info("\"REQUEST JOIN THE ROOM\"");

        RoomReadUseCase.FindRoomResult result = participantOperationUseCase.requestJoinRoom(request.getRoom_code());

        return ResponseEntity.created(null).body(RoomView.builder().roomResult(result).build());
    }

    @GetMapping(value = "/{room_id}")
    public ResponseEntity<List<ParticipantView>> showOneRoom(@PathVariable Long room_id,
                                                             @RequestParam(value = "accepted") Boolean accepted) {

        if (accepted == null) {
            throw new GotBetterException(MessageType.BAD_REQUEST);
        } else if (accepted) {
            log.info("\"ROOM MEMBER LIST\"");
        } else {
            log.info("\"WAIT LIST FOR APPROVE\"");
        }

        List<ParticipantView> participantViews = new ArrayList<>();
        List<ParticipantReadUseCase.FindParticipantResult> result = participantReadUseCase.getMemberListInARoom(room_id, accepted);
        for (ParticipantReadUseCase.FindParticipantResult r : result) {
            participantViews.add(ParticipantView.builder().participantResult(r).build());
        }

        return ResponseEntity.ok(participantViews);
    }

    @PatchMapping(value = "")
    public ResponseEntity<ParticipantView> approveJoinRoom(@Valid @RequestBody ParticipantJoinApproveRequest request) {

        log.info("\"APPROVE JOIN ROOM\"");

        var command = ParticipantOperationUseCase.UserRoomAcceptedUpdateCommand.builder()
                .userId(request.getUser_id())
                .roomId(request.getRoom_id())
                .build();
        ParticipantReadUseCase.FindParticipantResult result = participantOperationUseCase.approveJoinRoom(command);

        return ResponseEntity.ok(ParticipantView.builder().participantResult(result).build());
    }

    @GetMapping(value = "/{participant_id}/refund")
    public ResponseEntity<RefundView> getRefund(@PathVariable Long participant_id) {

        log.info("\"GET MY REFUND\"");

        Integer refund = participantReadUseCase.getMyRefund(participant_id);

        return ResponseEntity.ok(RefundView.builder().refund(refund).build());
    }
}