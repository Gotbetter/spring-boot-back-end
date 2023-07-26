package pcrc.gotbetter.participant.data_access.view;

import java.time.LocalDate;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Immutable
@Table(name = "ParticipantView")
@Getter
public class EnteredView {
	@Id
	@Column(name = "participant_id")
	private Long participantId;
	private Boolean authority;
	@Column(name = "percent_sum")
	private Float percentSum;
	private Integer refund;

	@Column(name = "user_id")
	private Long userId;
	@Column(name = "username")
	private String usernameNick;
	private String email;
	private String profile;

	@Column(name = "room_id")
	private Long roomId;
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
	@Column(name = "total_entry_fee")
	private Integer totalEntryFee;
	private String rule;
}
