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

	// room
	private Room room;

	// user
	private User user;

	// userset
	private String authId;

	public ParticipantDto(Participant participant, User user, String authId) {
		this.participant = participant;
		this.user = user;
		this.authId = authId;
	}

	public ParticipantDto(Participant participant, Room room) {
		this.participant = participant;
		this.room = room;
	}

	public ParticipantDto(Participant participant, Room room, User user) {
		this.participant = participant;
		this.room = room;
		this.user = user;
	}

	public ParticipantDto(Participant participant, User user) {
		this.participant = participant;
		this.user = user;
	}
}