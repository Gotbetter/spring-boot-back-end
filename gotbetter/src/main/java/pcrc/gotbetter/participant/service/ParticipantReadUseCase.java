package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;

import java.util.List;

public interface ParticipantReadUseCase {
    List<FindParticipantResult> getMemberListInARoom(Long room_id, Boolean accepted);

    @Getter
    @ToString
    @Builder
    class FindParticipantResult {
        private final Long participant_id;
        private final Long user_id;
        private final String auth_id;
        private final String username;
        private final String email;
        private final String profile;
        private final Boolean authority;

        public static FindParticipantResult findByParticipant(EnteredView view) {
            return FindParticipantResult.builder()
                    .participant_id(view.getParticipantId())
                    .user_id(view.getUserId())
                    .auth_id(view.getAuthId())
                    .username(view.getUsernameNick())
                    .email(view.getEmail())
                    .profile(view.getProfile())
                    .authority(view.getAuthority())
                    .build();
        }

        public static FindParticipantResult findByParticipant(TryEnterView view, Long participant_id,
                                                              Boolean authority) {
            return FindParticipantResult.builder()
                    .participant_id(participant_id)
                    .user_id(view.getUserId())
                    .auth_id(view.getAuthId())
                    .username(view.getUsernameNick())
                    .email(view.getEmail())
                    .profile(view.getProfile())
                    .authority(authority)
                    .build();
        }
    }
}
