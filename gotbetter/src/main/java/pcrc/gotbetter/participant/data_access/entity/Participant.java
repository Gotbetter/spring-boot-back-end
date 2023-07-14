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
@Table(name = "Participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Participant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "room_id")
    private Long roomId;
    private Boolean authority;
    @Column(name = "percent_sum")
    private Float percentSum;
    private Integer refund;

    @Builder
    public Participant(Long participantId, Long userId, Long roomId,
                       Boolean authority, Float percentSum, Integer refund) {
        this.participantId = participantId;
        this.userId = userId;
        this.roomId = roomId;
        this.authority = authority;
        this.percentSum = percentSum;
        this.refund = refund;
    }
}