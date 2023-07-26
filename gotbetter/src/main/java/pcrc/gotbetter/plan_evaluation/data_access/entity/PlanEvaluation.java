package pcrc.gotbetter.plan_evaluation.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.BaseTimeEntity;

@Entity
@Table(name = "PlanEvaluation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class PlanEvaluation extends BaseTimeEntity {
	@EmbeddedId
	private PlanEvaluationId planEvaluationId;

	@Builder
	public PlanEvaluation(PlanEvaluationId planEvaluationId) {
		this.planEvaluationId = planEvaluationId;
	}
}
