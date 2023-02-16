package pcrc.gotbetter.plan_evaluation.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "PlanEvaluation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class PlanEvaluation {
    @EmbeddedId
    private PlanEvaluationId planEvaluationId;

    @Builder
    public PlanEvaluation(PlanEvaluationId planEvaluationId) {
        this.planEvaluationId = planEvaluationId;
    }
}
