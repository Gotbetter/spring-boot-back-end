package pcrc.gotbetter.participant.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipateId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "room_id")
    private Long roomId;

    @Builder
    public ParticipateId(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }
}
