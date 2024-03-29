package pcrc.gotbetter.plan.data_access.entity;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;
import pcrc.gotbetter.setting.common.BaseTimeEntity;

@Entity
@Table(name = "Plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Plan extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plan_id")
	private Long planId;
	@Embedded
	private ParticipantInfo participantInfo;
	@Column(name = "start_date")
	private LocalDate startDate;
	@Column(name = "target_date")
	private LocalDate targetDate;
	@Column(name = "score")
	private Float score;
	private Integer week;
	@Column(name = "three_days_passed")
	private Boolean threeDaysPassed;
	private Boolean rejected;

	@Builder
	public Plan(
		Long planId,
		ParticipantInfo participantInfo,
		LocalDate startDate,
		LocalDate targetDate,
		Float score,
		Integer week,
		Boolean threeDaysPassed,
		Boolean rejected
	) {
		this.planId = planId;
		this.participantInfo = participantInfo;
		this.startDate = startDate;
		this.targetDate = targetDate;
		this.score = score;
		this.week = week;
		this.threeDaysPassed = threeDaysPassed;
		this.rejected = rejected;
	}

	public void updateRejected(Boolean rejected) {
		this.rejected = rejected;
	}

	public void updateThreeDaysPassed() {
		this.threeDaysPassed = true;
	}

	public void updateById(String userId) {
		this.updateCreatedById(userId);
		this.updateUpdatedById(userId);
	}

	public void updateScore(Float score) {
		this.score = score;
	}
}
