package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.user.data_access.entity.User;

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

        public static FindParticipantResult findByParticipant(User user, Long participant_id) {
            return FindParticipantResult.builder()
                    .participant_id(participant_id)
                    .user_id(user.getUserId())
                    .auth_id(user.getAuthId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .profile(user.getProfile())
                    .build();
        }
    }
}
