package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.repository.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.view.EnteredView;

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

        public static FindParticipantResult findByParticipant(JoinRequestDto joinRequestResultDTO,
            Long participantId, Boolean authority) {
            return FindParticipantResult.builder()
                .participantId(participantId)
                .userId(joinRequestResultDTO.getUser().getUserId())
                .authId(joinRequestResultDTO.getUserSet().getAuthId())
                .username(joinRequestResultDTO.getUser().getUsername())
                .email(joinRequestResultDTO.getUser().getEmail())
                .profile(joinRequestResultDTO.getUser().getProfile())
                .authority(authority)
                .build();
        }
    }
}
