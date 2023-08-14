package pcrc.gotbetter.participant.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.common.BaseTimeEntity;

@Entity
@Table(name = "Participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Participant extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "participant_id", nullable = false)
	private Long participantId;
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "room_id", nullable = false)
	private Long roomId;
	@Column(nullable = false)
	private Boolean authority;
	@Column(name = "percent_sum", nullable = false)
	private Float percentSum;
	@Column(nullable = false)
	private Integer refund;

	@Builder
	public Participant(
		Long participantId,
		Long userId,
		Long roomId,
		Boolean authority,
		Float percentSum,
		Integer refund) {
		this.participantId = participantId;
		this.userId = userId;
		this.roomId = roomId;
		this.authority = authority;
		this.percentSum = percentSum;
		this.refund = refund;
	}

	public void updatePercentSum(Float percentSum) {
		this.percentSum += percentSum;
	}

	public void updateRefund(Integer refund) {
		this.refund = refund;
	}

	public void updateById(String userId) {
		this.updateCreatedById(userId);
		this.updateUpdatedById(userId);
	}

	public void updateAuthority(Boolean authority) {
		this.authority = authority;
	}
}