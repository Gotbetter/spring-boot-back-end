package pcrc.gotbetter.participant.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.user.data_access.entity.User;

@Getter
@AllArgsConstructor
public class ParticipantDto {
	// participant
	private Participant participant;
	private Long participantId;
	private Boolean authority;

	// room
	private Room room;
	private Long roomId;
	private Integer currentUserNum;
	private Integer currentWeek;

	// user
	private User user;
	private Long userId;

	// userset
	private String authId;

	// ParticipantService
	public ParticipantDto(Long participantId, Boolean authority,
		User user, String authId) {
		this.participantId = participantId;
		this.authority = authority;
		this.user = user;
		this.authId = authId;
	}

	// DetailPlanEvalService
	// public ParticipantDto(Long participantId, Long roomId,
	// 	Integer currentUserNum, Integer currentWeek, Long userId) {
	// 	this.participantId = participantId;
	// 	this.roomId = roomId;
	// 	this.currentUserNum = currentUserNum;
	// 	this.currentWeek = currentWeek;
	// 	this.userId = userId;
	// }
	//
	// public ParticipantDto(Participant participant, Room room) {
	// 	this.participant = participant;
	// 	this.room = room;
	// }
}