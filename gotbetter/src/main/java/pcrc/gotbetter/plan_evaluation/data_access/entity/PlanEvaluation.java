package pcrc.gotbetter.plan_evaluation.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.BaseTimeEntity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
