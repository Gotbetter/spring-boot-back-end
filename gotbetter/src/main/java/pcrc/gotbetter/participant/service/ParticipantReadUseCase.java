package pcrc.gotbetter.participant.service;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;

public interface ParticipantReadUseCase {
	List<FindParticipantResult> getMemberListInARoom(ParticipantsFindQuery query) throws IOException;

	Integer getMyRefund(Long participantId);

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class ParticipantsFindQuery {
		private final Long roomId;
		private final Boolean accepted;
		private final Boolean admin;
	}

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
		// for admin
		private final Float percentSum;
		private final Integer refund;
		private final String createdDate;
		private final String updatedDate;

		public static FindParticipantResult findByParticipant(
			ParticipantDto participantDto,
			String profile,
			Boolean isAdmin
		) {
			return FindParticipantResult.builder()
				.participantId(participantDto.getParticipant().getParticipantId())
				.userId(participantDto.getUser().getUserId())
				.authId(participantDto.getAuthId())
				.username(participantDto.getUser().getUsername())
				.email(participantDto.getUser().getEmail())
				.profile(profile)
				.authority(participantDto.getParticipant().getAuthority())
				.percentSum(isAdmin ? Math.round(participantDto.getParticipant().getPercentSum() * 1000) / 10.0F : null)
				.refund(isAdmin ? participantDto.getParticipant().getRefund() : null)
				.updatedDate(isAdmin ? participantDto.getParticipant().getUpdatedDate().toLocalDate().toString() : null)
				.build();
		}

		public static FindParticipantResult findByParticipant(
			JoinRequestDto joinRequestResultDTO,
			Long participantId,
			Boolean authority,
			String profile,
			Boolean isAdmin
		) {
			return FindParticipantResult.builder()
				.participantId(participantId)
				.userId(joinRequestResultDTO.getUser().getUserId())
				.authId(joinRequestResultDTO.getUserSet().getAuthId())
				.username(joinRequestResultDTO.getUser().getUsername())
				.email(joinRequestResultDTO.getUser().getEmail())
				.profile(profile)
				.authority(authority)
				.createdDate(
					isAdmin ? joinRequestResultDTO.getJoinRequest().getUpdatedDate().toLocalDate().toString() : null)
				.build();
		}
	}
}
