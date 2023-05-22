package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;

import java.util.List;

public interface ParticipantReadUseCase {
    List<FindParticipantResult> getMemberListInARoom(Long roomId, Boolean accepted);
    Integer getMyRefund(Long participantId);

    @Getter
    @ToString
    @Builder
    class FindParticipantResult {
        private final Long participantId;
        private final Long userId;
        private final String authId;
        private final String username;
        private final String email;
        private final String profile;
        private final Boolean authority;

        public static FindParticipantResult findByParticipant(EnteredView view, String authId) {
            return FindParticipantResult.builder()
                    .participantId(view.getParticipantId())
                    .userId(view.getUserId())
                    .authId(authId)
                    .username(view.getUsernameNick())
                    .email(view.getEmail())
                    .profile(view.getProfile())
                    .authority(view.getAuthority())
                    .build();
        }

        public static FindParticipantResult findByParticipant(TryEnterView view, Long participantId,
                                                              Boolean authority, String authId) {
            return FindParticipantResult.builder()
                    .participantId(participantId)
                    .userId(view.getTryEnterId().getUserId())
                    .authId(authId)
                    .username(view.getUsernameNick())
                    .email(view.getEmail())
                    .profile(view.getProfile())
                    .authority(authority)
                    .build();
        }
    }
}
