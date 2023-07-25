package pcrc.gotbetter.room.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface RoomReadUseCase {

    List<FindRoomResult> getUserRoomList();
    FindRoomResult getOneRoomInfo(Long roomId);
    List<FindRankResult> getRank(Long roomId) throws IOException;

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

        public static FindRoomResult findByRoom(JoinRequestDto joinRequestDto, String roomCategory, String rule) {
            return FindRoomResult.builder()
                .roomId(joinRequestDto.getRoom().getRoomId())
                .title(joinRequestDto.getRoom().getTitle())
                .maxUserNum(joinRequestDto.getRoom().getMaxUserNum())
                .currentUserNum(joinRequestDto.getRoom().getCurrentUserNum())
                .startDate(joinRequestDto.getRoom().getStartDate().toString())
                .week(joinRequestDto.getRoom().getWeek())
                .currentWeek(joinRequestDto.getRoom().getCurrentWeek())
                .entryFee(joinRequestDto.getRoom().getEntryFee())
                .roomCode(joinRequestDto.getRoom().getRoomCode())
                .account(joinRequestDto.getRoom().getAccount())
                .roomCategory(roomCategory)
                .description(joinRequestDto.getRoom().getDescription() == null ? "" : joinRequestDto.getRoom().getDescription())
                .totalEntryFee(joinRequestDto.getRoom().getTotalEntryFee())
                .rule(rule)
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
