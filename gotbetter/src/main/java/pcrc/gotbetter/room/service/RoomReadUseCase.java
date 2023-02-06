package pcrc.gotbetter.room.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.data_access.entity.Room;

import java.util.Date;
import java.util.List;

public interface RoomReadUseCase {

    List<FindRoomResult> getUserRooms();
    FindRoomResult getOneRoomInfo(Long room_id);

    @Getter
    @ToString
    @Builder
    class FindRoomResult {
        private final Long room_id;
        private final String title;
        private final Integer max_user_num;
        private final Date start_date;
        private final Date target_date;
        private final Integer entry_fee;
        private final String room_code;
        private final Long leader_id;
        private final String account;
        private final Integer total_entry_fee;
        private final Integer rule_id;

        public static FindRoomResult findByRoom(Room room) {
            return FindRoomResult.builder()
                    .room_id(room.getRoomId())
                    .title(room.getTitle())
                    .max_user_num(room.getMaxUserNum())
                    .start_date(room.getStartDate())
                    .target_date(room.getTargetDate())
                    .entry_fee(room.getEntryFee())
                    .room_code(room.getRoomCode())
                    .leader_id(room.getLeaderId())
                    .account(room.getAccount())
                    .total_entry_fee(room.getTotalEntryFee())
                    .rule_id(room.getRuleId())
                    .build();
        }
    }
}
