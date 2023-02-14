package pcrc.gotbetter.detail_plan.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "DetailPlan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class DetailPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String content;
    private Boolean complete;

    @Builder
    public DetailPlan(Long detailPlanId, Long planId, Long participantId,
                      Long userId, Long roomId, String content, Boolean complete) {
        this.detailPlanId = detailPlanId;
        this.planId = planId;
        this.participantId = participantId;
        this.userId = userId;
        this.roomId = roomId;
        this.content = content;
        this.complete = complete;
    }
}
