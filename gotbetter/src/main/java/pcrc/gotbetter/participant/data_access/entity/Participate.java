package pcrc.gotbetter.participant.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "Participate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Participate {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "room_id")
    private Long roomId;
    private Boolean accepted;

    @Builder
    public Participate(Long userId, Long roomId, Boolean accepted) {
        this.userId = userId;
        this.roomId = roomId;
        this.accepted = accepted;
    }
}
