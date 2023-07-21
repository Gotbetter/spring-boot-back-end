package pcrc.gotbetter.detail_plan_record.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailPlanId {
	@Column(name = "detail_plan_id", nullable = false)
	private Long detailPlanId;
	@Column(name = "plan_id", nullable = false)
	private Long planId;
	@Column(name = "participant_id", nullable = false)
	private Long participantId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "room_id", nullable = false)
	private Long roomId;

	@Builder
	public DetailPlanId(Long detailPlanId, Long planId, Long participantId, Long userId, Long roomId) {
		this.detailPlanId = detailPlanId;
		this.planId = planId;
		this.participantId = participantId;
		this.userId = userId;
		this.roomId = roomId;
	}
}
