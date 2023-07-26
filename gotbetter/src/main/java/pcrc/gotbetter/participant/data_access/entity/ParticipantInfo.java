package pcrc.gotbetter.participant.data_access.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantInfo implements Serializable {
	@Column(name = "participant_id")
	private Long participantId;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "room_id")
	private Long roomId;

	@Builder
	public ParticipantInfo(Long participantId, Long userId, Long roomId) {
		this.participantId = participantId;
		this.userId = userId;
		this.roomId = roomId;
	}
}
