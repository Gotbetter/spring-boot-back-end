package pcrc.gotbetter.detail_plan.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;

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
    @Embedded
    private ParticipantInfo participantInfo;
    private String content;
    private Boolean complete;
    private String approve_comment;
    private Boolean rejected;

    @Builder
    public DetailPlan(Long detailPlanId, Long planId,
                      ParticipantInfo participantInfo,
                      String content, Boolean complete,
                      String approve_comment, Boolean rejected) {
        this.detailPlanId = detailPlanId;
        this.planId = planId;
        this.participantInfo = participantInfo;
        this.content = content;
        this.complete = complete;
        this.approve_comment = approve_comment;
        this.rejected = rejected;
    }
}
