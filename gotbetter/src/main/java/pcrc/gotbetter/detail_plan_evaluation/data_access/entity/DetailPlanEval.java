package pcrc.gotbetter.detail_plan_evaluation.data_access.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "DetailPlanEvaluation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class DetailPlanEval {
    @EmbeddedId
    private DetailPlanEvalId detailPlanEvalId;

    @Builder
    public DetailPlanEval(DetailPlanEvalId detailPlanEvalId) {
        this.detailPlanEvalId = detailPlanEvalId;
    }
}
