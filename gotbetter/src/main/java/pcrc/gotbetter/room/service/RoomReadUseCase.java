package pcrc.gotbetter.room.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.room.data_access.entity.Room;

public interface RoomReadUseCase {

	List<FindRoomResult> getUserRoomList(Boolean admin);

	FindRoomResult getOneRoomInfo(RoomFindQuery query);

	List<FindRankResult> getRank(Long roomId) throws IOException;

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@ToString
	@Builder
	class RoomFindQuery {
		private final Long roomId;
		private final Boolean admin;
	}

	@Getter
	@ToString
	@Builder
	class FindRoomResult {
		private final Long roomId;
		private final String title;
		private final Integer maxUserNum;
		private final Integer currentUserNum;
		private final String startDate;
		private final Integer week;
		private final Integer currentWeek;
		private final Integer entryFee;
		private final String roomCode;
		private final String account;
		private final String roomCategory;
		private final String description;
		private final Integer totalEntryFee;
		private final String rule;
		private final Long participantId;
		// for admin
		private final String leader;
		private final String endDate;

		public static FindRoomResult findByRoom(Room room, Long participantId, String roomCategory, String rule) {
			return FindRoomResult.builder()
				.roomId(room.getRoomId())
				.title(room.getTitle())
				.maxUserNum(room.getMaxUserNum())
				.currentUserNum(room.getCurrentUserNum())
				.startDate(room.getStartDate().toString())
				.week(room.getWeek())
				.currentWeek(room.getCurrentWeek())
				.entryFee(room.getEntryFee())
				.roomCode(room.getRoomCode())
				.account(room.getAccount())
				.roomCategory(roomCategory)
				.description(room.getDescription() == null ? "" : room.getDescription())
				.totalEntryFee(room.getTotalEntryFee())
				.rule(rule)
				.participantId(participantId)
				.build();
		}

		public static FindRoomResult findByRoom(ParticipantDto participantDto, String roomCategory, String rule) {
			return FindRoomResult.builder()
				.roomId(participantDto.getRoom().getRoomId())
				.title(participantDto.getRoom().getTitle())
				.maxUserNum(participantDto.getRoom().getMaxUserNum())
				.currentUserNum(participantDto.getRoom().getCurrentUserNum())
				.startDate(participantDto.getRoom().getStartDate().toString())
				.week(participantDto.getRoom().getWeek())
				.currentWeek(participantDto.getRoom().getCurrentWeek())
				.entryFee(participantDto.getRoom().getEntryFee())
				.roomCode(participantDto.getRoom().getRoomCode())
				.account(participantDto.getRoom().getAccount())
				.roomCategory(roomCategory)
				.description(
					participantDto.getRoom().getDescription() == null ? "" : participantDto.getRoom().getDescription())
				.totalEntryFee(participantDto.getRoom().getTotalEntryFee())
				.rule(rule)
				.build();
		}

		public static FindRoomResult findByRoom(
			ParticipantDto participantDto,
			String roomCategory,
			String rule,
			String endDate
		) {
			return FindRoomResult.builder()
				.roomId(participantDto.getRoom().getRoomId())
				.title(participantDto.getRoom().getTitle())
				.maxUserNum(participantDto.getRoom().getMaxUserNum())
				.currentUserNum(participantDto.getRoom().getCurrentUserNum())
				.startDate(participantDto.getRoom().getStartDate().toString())
				.week(participantDto.getRoom().getWeek())
				.currentWeek(participantDto.getRoom().getCurrentWeek())
				.entryFee(participantDto.getRoom().getEntryFee())
				.roomCode(participantDto.getRoom().getRoomCode())
				.account(participantDto.getRoom().getAccount())
				.roomCategory(roomCategory)
				.description(
					participantDto.getRoom().getDescription() == null ? "" : participantDto.getRoom().getDescription())
				.totalEntryFee(participantDto.getRoom().getTotalEntryFee())
				.rule(rule)
				.leader(participantDto.getUser().getUsername())
				.endDate(endDate)
				.build();
		}
	}

	@Getter
	@ToString
	@Builder
	class FindRankResult {
		private final Integer rankId;
		private final Integer rank;
		private final String username;
		private final String profile;
		private final Integer refund;

		public static FindRankResult findByRank(Integer rankId, Integer rank,
			HashMap<String, String> userInfo, Integer refund) {
			return FindRankResult.builder()
				.rankId(rankId)
				.rank(rank)
				.username(userInfo.get("username"))
				.profile(userInfo.get("profile"))
				.refund(refund)
				.build();
		}
	}
}
