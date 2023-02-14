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
    @Id
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "participant_id")
    private Long participantId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "room_id")
    private Long roomId;

    @Builder
    public PlanEvaluation(Long planId, Long participantId, Long userId, Long roomId) {
        this.planId = planId;
        this.participantId =  participantId;
        this.userId = userId;
        this.roomId = roomId;
    }
}
