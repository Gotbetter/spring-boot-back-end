package pcrc.gotbetter.participant.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;

public interface ParticipantReadUseCase {
	List<FindParticipantResult> getMemberListInARoom(Long roomId, Boolean accepted) throws IOException;

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

		public static FindParticipantResult findByParticipant(ParticipantDto participantDto, String profile) {
			return FindParticipantResult.builder()
				.participantId(participantDto.getParticipantId())
				.userId(participantDto.getUser().getUserId())
				.authId(participantDto.getAuthId())
				.username(participantDto.getUser().getUsername())
				.email(participantDto.getUser().getEmail())
				.profile(profile)
				.authority(participantDto.getAuthority())
				.build();
		}

		public static FindParticipantResult findByParticipant(
			JoinRequestDto joinRequestResultDTO,
			Long participantId,
			Boolean authority,
			String profile
		) {
			return FindParticipantResult.builder()
				.participantId(participantId)
				.userId(joinRequestResultDTO.getUser().getUserId())
				.authId(joinRequestResultDTO.getUserSet().getAuthId())
				.username(joinRequestResultDTO.getUser().getUsername())
				.email(joinRequestResultDTO.getUser().getEmail())
				.profile(profile)
				.authority(authority)
				.build();
		}
	}
}
