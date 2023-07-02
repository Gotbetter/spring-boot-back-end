package pcrc.gotbetter.participant.data_access.view;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import pcrc.gotbetter.participant.data_access.entity.ParticipateId;

import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "ParticipateView")
@Getter
public class TryEnterView {
    private Boolean accepted;

    @EmbeddedId
    ParticipateId tryEnterId;

    @Column(name = "username")
    private String usernameNick;
    private String email;
    private String profile;

    private String title;
    @Column(name = "max_user_num")
    private Integer maxUserNum;
    @Column(name = "current_user_num")
    private Integer currentUserNum;
    @Column(name = "start_date")
    private LocalDate startDate;
    private Integer week;
    @Column(name = "current_week")
    private Integer currentWeek;
    @Column(name = "entry_fee")
    private Integer entryFee;
    @Column(name = "room_code")
    private String roomCode;
    private String account;
    private String description;
    @Column(name = "total_entry_fee")
    private Integer totalEntryFee;
    @Column(name = "rule_id")
    private Integer ruleId;
}
