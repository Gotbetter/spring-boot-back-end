package pcrc.gotbetter.plan_evaluation.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanEvaluationId {
	@Column(name = "plan_id")
	private Long planId;
	@Column(name = "participant_id")
	private Long participantId;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "room_id")
	private Long roomId;

	@Builder
	public PlanEvaluationId(Long planId, Long participantId, Long userId, Long roomId) {
		this.planId = planId;
		this.participantId = participantId;
		this.userId = userId;
		this.roomId = roomId;
	}
}
