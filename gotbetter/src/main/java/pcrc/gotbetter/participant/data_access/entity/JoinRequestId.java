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
public class JoinRequestId implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Builder
    public JoinRequestId(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }
}
