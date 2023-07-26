package pcrc.gotbetter.participant.data_access.view;

import java.time.LocalDate;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;

@Entity
@Immutable
@Table(name = "join_request_view")
@Getter
public class TryEnterView {
	private Boolean accepted;

	@EmbeddedId
	JoinRequestId tryEnterId;

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
	@Column(name = "room_category")
	private String roomCategory;
	private String description;
	@Column(name = "total_entry_fee")
	private Integer totalEntryFee;
	private String rule;
}
