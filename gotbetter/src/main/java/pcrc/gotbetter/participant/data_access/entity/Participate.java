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
@Table(name = "Participate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Participate extends BaseTimeEntity {
    @EmbeddedId
    private ParticipateId participateId;
    private Boolean accepted;

    @Builder
    public Participate(ParticipateId participateId, Boolean accepted) {
        this.participateId = participateId;
        this.accepted = accepted;
    }
}
