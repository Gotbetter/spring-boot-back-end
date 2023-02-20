package pcrc.gotbetter.detail_plan_evaluation.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailPlanEvalId {
    @Column(name = "detail_plan_id")
    private Long detailPlanId;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "participant_id")
    private Long participantId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "room_id")
    private Long roomId;

    @Builder
    public DetailPlanEvalId(Long detailPlanId, Long planId, Long participantId,
                            Long userId, Long roomId) {
        this.detailPlanId = detailPlanId;
        this.planId = planId;
        this.participantId =  participantId;
        this.userId = userId;
        this.roomId = roomId;
    }
}
