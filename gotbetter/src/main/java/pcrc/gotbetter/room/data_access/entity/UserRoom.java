package pcrc.gotbetter.room.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "UserRoom")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class UserRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_room_id")
    private Long userRoomId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "room_id")
    private Long roomId;
    @Column(name = "percent_sum")
    private Float percentSum;
    private Integer refund;
    private Boolean accepted;

    @Builder
    public UserRoom(Long userRoomId, Long userId, Long roomId,
                    Float percentSum, Integer refund, Boolean accepted) {
        this.userRoomId = userRoomId;
        this.userId = userId;
        this.roomId = roomId;
        this.percentSum = percentSum;
        this.refund = refund;
        this.accepted = accepted;
    }
}