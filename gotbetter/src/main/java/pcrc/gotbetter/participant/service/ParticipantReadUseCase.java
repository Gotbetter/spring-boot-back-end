package pcrc.gotbetter.participant.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;

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

        public static FindParticipantResult findByParticipant(ParticipantDto participantDto) {
            return FindParticipantResult.builder()
                .participantId(participantDto.getParticipantId())
                .userId(participantDto.getUser().getUserId())
                .authId(participantDto.getAuthId())
                .username(participantDto.getUser().getUsername())
                .email(participantDto.getUser().getEmail())
                .profile(participantDto.getUser().getProfile())
                .authority(participantDto.getAuthority())
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
