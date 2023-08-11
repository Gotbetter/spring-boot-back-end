package pcrc.gotbetter.participant.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.participant.service.ParticipantOperationUseCase;
import pcrc.gotbetter.participant.service.ParticipantReadUseCase;
import pcrc.gotbetter.participant.ui.requestBody.ParticipantJoinApproveRequest;
import pcrc.gotbetter.participant.ui.requestBody.ParticipantJoinRequest;
import pcrc.gotbetter.participant.ui.view.ParticipantView;
import pcrc.gotbetter.participant.ui.view.RefundView;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.room.ui.view.RoomView;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

@Slf4j
@RestController
@RequestMapping(value = "/participants")
public class ParticipantController {
	private final ParticipantOperationUseCase participantOperationUseCase;
	private final ParticipantReadUseCase participantReadUseCase;

	@Autowired
	public ParticipantController(
		ParticipantOperationUseCase participantOperationUseCase,
		ParticipantReadUseCase participantReadUseCase
	) {
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
	public ResponseEntity<List<ParticipantView>> getUserListAboutRoom(
		@PathVariable Long room_id,
		@RequestParam(value = "accepted") Boolean accepted,
		@RequestParam(name = "admin", required = false) Boolean admin
	) throws IOException {

		if (accepted == null) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		} else if (accepted) {
			log.info("\"ROOM MEMBER LIST\"");
		} else {
			log.info("\"WAIT LIST FOR APPROVE\"");
		}

		var query = ParticipantReadUseCase.ParticipantsFindQuery.builder()
			.roomId(room_id)
			.accepted(accepted)
			.admin(admin != null && admin)
			.build();
		List<ParticipantReadUseCase.FindParticipantResult> result = participantReadUseCase.getMemberListInARoom(query);
		List<ParticipantView> participantViews = new ArrayList<>();
		for (ParticipantReadUseCase.FindParticipantResult r : result) {
			participantViews.add(ParticipantView.builder().participantResult(r).build());
		}

		return ResponseEntity.ok(participantViews);
	}

	@PatchMapping(value = "")
	public ResponseEntity<ParticipantView> approveJoinRoom(
		@Valid @RequestBody ParticipantJoinApproveRequest request
	) throws IOException {

		log.info("\"APPROVE JOIN ROOM\"");

		var command = ParticipantOperationUseCase.UserRoomAcceptedCommand.builder()
			.userId(request.getUser_id())
			.roomId(request.getRoom_id())
			.build();
		ParticipantReadUseCase.FindParticipantResult result = participantOperationUseCase.approveJoinRoom(command);

		return ResponseEntity.ok(ParticipantView.builder().participantResult(result).build());
	}

	@PostMapping(value = "/reject")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rejectJoinRoom(@Valid @RequestBody ParticipantJoinApproveRequest request) {

		log.info("\"REJECT JOIN ROOM\"");

		var command = ParticipantOperationUseCase.UserRoomAcceptedCommand.builder()
			.userId(request.getUser_id())
			.roomId(request.getRoom_id())
			.build();

		participantOperationUseCase.rejectJoinRoom(command);
	}

	@GetMapping(value = "/{participant_id}/refund")
	public ResponseEntity<RefundView> getRefund(@PathVariable Long participant_id) {

		log.info("\"GET MY REFUND\"");

		Integer refund = participantReadUseCase.getMyRefund(participant_id);

		return ResponseEntity.ok(RefundView.builder().refund(refund).build());
	}
}
