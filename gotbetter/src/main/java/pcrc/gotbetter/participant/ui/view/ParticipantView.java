package pcrc.gotbetter.participant.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.service.ParticipantReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantView {
	private final Long participant_id;
	private final Long user_id;
	private final String auth_id;
	private final String username;
	private final String email;
	private final String profile;
	private final Boolean authority;
	// for admin
	private final Float percent_sum;
	private final Integer refund;
	private final String created_date;
	private final String updated_date;

	@Builder
	public ParticipantView(ParticipantReadUseCase.FindParticipantResult participantResult) {
		this.participant_id = participantResult.getParticipantId();
		this.user_id = participantResult.getUserId();
		this.auth_id = participantResult.getAuthId();
		this.username = participantResult.getUsername();
		this.email = participantResult.getEmail();
		this.profile = participantResult.getProfile();
		this.authority = participantResult.getAuthority();
		this.percent_sum = participantResult.getPercentSum();
		this.refund = participantResult.getRefund();
		this.created_date = participantResult.getCreatedDate();
		this.updated_date = participantResult.getUpdatedDate();
	}
}
