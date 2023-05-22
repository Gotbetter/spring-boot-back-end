package pcrc.gotbetter.room.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.List;

public interface RoomReadUseCase {

    List<FindRoomResult> getUserRooms();
    FindRoomResult getOneRoomInfo(Long roomId);
    List<FindRankResult> getRank(Long roomId);

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
        private final Integer totalEntryFee;
        private final Integer ruleId;
        private final Long participantId;

        public static FindRoomResult findByRoom(Room room, Long participantId) {
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
                    .totalEntryFee(room.getTotalEntryFee())
                    .ruleId(room.getRuleId())
                    .participantId(participantId)
                    .build();
        }

        public static FindRoomResult findByRoom(TryEnterView tryEnterView) {
            return FindRoomResult.builder()
                    .roomId(tryEnterView.getTryEnterId().getRoomId())
                    .title(tryEnterView.getTitle())
                    .maxUserNum(tryEnterView.getMaxUserNum())
                    .currentUserNum(tryEnterView.getCurrentUserNum())
                    .startDate(tryEnterView.getStartDate().toString())
                    .week(tryEnterView.getWeek())
                    .currentWeek(tryEnterView.getCurrentWeek())
                    .entryFee(tryEnterView.getEntryFee())
                    .roomCode(tryEnterView.getRoomCode())
                    .account(tryEnterView.getAccount())
                    .totalEntryFee(tryEnterView.getTotalEntryFee())
                    .ruleId(tryEnterView.getRuleId())
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    class FindRankResult {
        private final String username;
        private final Integer rank;
        private final Integer refund;

        public static FindRankResult findByRank(String username, Integer rank, Integer refund) {
            return FindRankResult.builder()
                    .username(username)
                    .rank(rank)
                    .refund(refund)
                    .build();
        }
    }
}
