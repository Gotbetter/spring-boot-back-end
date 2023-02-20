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

    @Builder
    public ParticipantView(ParticipantReadUseCase.FindParticipantResult participantResult) {
        this.participant_id = participantResult.getParticipant_id();
        this.user_id = participantResult.getUser_id();
        this.auth_id = participantResult.getAuth_id();
        this.username = participantResult.getUsername();
        this.email = participantResult.getEmail();
        this.profile = participantResult.getProfile();
        this.authority = participantResult.getAuthority();
    }
}
