package pcrc.gotbetter.detail_plan_evaluation.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.common.BaseTimeEntity;

@Entity
@Table(name = "DetailPlanEvaluation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class DetailPlanEval extends BaseTimeEntity {
	@EmbeddedId
	private DetailPlanEvalId detailPlanEvalId;

	@Builder
	public DetailPlanEval(DetailPlanEvalId detailPlanEvalId) {
		this.detailPlanEvalId = detailPlanEvalId;
	}
}
