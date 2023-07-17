package pcrc.gotbetter.participant.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.BaseTimeEntity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "join_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class JoinRequest extends BaseTimeEntity {
    @EmbeddedId
    private JoinRequestId joinRequestId;
    @Column(nullable = false)
    private Boolean accepted;

    @Builder
    public JoinRequest(JoinRequestId joinRequestId, Boolean accepted) {
        this.joinRequestId = joinRequestId;
        this.accepted = accepted;
    }

    public void updateAcceptedToJoin() {
        this.accepted = true;
    }
}
