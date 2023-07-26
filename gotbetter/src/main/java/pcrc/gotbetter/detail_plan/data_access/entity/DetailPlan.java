package pcrc.gotbetter.detail_plan.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;
import pcrc.gotbetter.setting.BaseTimeEntity;

@Entity
@Table(name = "DetailPlan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class DetailPlan extends BaseTimeEntity {
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
    private Boolean rejected;

    @Builder
    public DetailPlan(Long detailPlanId, Long planId,
                      ParticipantInfo participantInfo,
                      String content, Boolean complete,
                      Boolean rejected) {
        this.detailPlanId = detailPlanId;
        this.planId = planId;
        this.participantInfo = participantInfo;
        this.content = content;
        this.complete = complete;
        this.rejected = rejected;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRejected(Boolean rejected) {
        this.rejected = rejected;
    }

    public void updateDetailPlanCompleted() {
        this.complete = true;
    }

    public void updateDetailPlanUndo(Boolean rejected) {
        this.complete = false;
        this.rejected = rejected;
    }

    public void updateById(String userId) {
        this.updateCreatedById(userId);
        this.updateUpdatedById(userId);
    }
}
