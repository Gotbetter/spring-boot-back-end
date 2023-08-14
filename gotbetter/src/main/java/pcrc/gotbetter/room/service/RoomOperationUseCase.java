package pcrc.gotbetter.room.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface RoomOperationUseCase {

	RoomReadUseCase.FindRoomResult createRoom(RoomCreateCommand command);

	RoomReadUseCase.FindRoomResult createRoomAdmin(RoomCreateCommand command);

	RoomReadUseCase.FindRoomResult updateDescriptionRoom(RoomUpdateDescriptionCommand command);

	void updateRoomInfo(RoomUpdateCommand command);

	void deleteRoom(Long roomId);

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class RoomCreateCommand {
		private final String title;
		private final Integer maxUserNum;
		private final String startDate;
		private final Integer week;
		private final Integer currentWeek;
		private final Integer entryFee;
		private final String ruleCode;
		private final String account;
		private final String roomCategoryCode;
		private final String description;
		private final Long userId;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class RoomUpdateDescriptionCommand {
		private final Long room_id;
		private final String description;
	}

	@EqualsAndHashCode(callSuper = false)
	@Builder
	@Getter
	@ToString
	class RoomUpdateCommand {
		private final Long roomId;
		private final String title;
		private final String account;
		private final Integer maxUserNum;
		private final String roomCategoryCode;
		private final String ruleCode;
		private final Long userId;
		// private final Integer week;
		// private final Integer entryFee;
		// private final String roomCode;
	}
}
