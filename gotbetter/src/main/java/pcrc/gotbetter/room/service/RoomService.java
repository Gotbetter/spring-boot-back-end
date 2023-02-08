package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.user_room.data_access.entity.UserRoom;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.user_room.data_access.repository.UserRoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class RoomService implements RoomOperationUseCase, RoomReadUseCase {
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, UserRoomRepository userRoomRepository) {
        this.roomRepository = roomRepository;
        this.userRoomRepository = userRoomRepository;
    }

    @Override
    public List<FindRoomResult> getUserRooms() {
        Long user_id = getCurrentUserId();
        List<Room> rooms = roomRepository.findUserRooms(user_id);
        List<FindRoomResult> result = new ArrayList<>();

        for (Room r : rooms) {
            result.add(FindRoomResult.builder()
                    .room_id(r.getRoomId())
                    .title(r.getTitle())
                    .build());
        }
        return result;
    }

    @Override
    public FindRoomResult getOneRoomInfo(Long room_id) {
        Long user_id = getCurrentUserId();
        Room room = roomRepository.findRoomWithUserIdAndRoomId(user_id, room_id);

        if (room == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return FindRoomResult.findByRoom(room);
    }

    @Override
    public FindRoomResult createRoom(RoomCreateCommand command) {
        Long user_id = getCurrentUserId();

        Room room = Room.builder()
                .title(command.getTitle())
                .maxUserNum(command.getMax_user_num())
                .currentUserNum(1)
                .startDate(command.getStart_date())
                .week(command.getWeek())
                .currentWeek(command.getCurrent_week())
                .entryFee(command.getEntry_fee())
                .roomCode(getRandomCode())
                .leaderId(user_id)
                .account(command.getAccount())
                .totalEntryFee(command.getEntry_fee())
                .ruleId(command.getRule_id())
                .build();
        roomRepository.save(room);

        UserRoom userRoom = UserRoom.builder()
                .userId(user_id)
                .roomId(room.getRoomId())
                .refund(room.getEntryFee())
                .accepted(true)
                .build();
        userRoomRepository.save(userRoom);

        return FindRoomResult.findByRoom(room);
    }

    /**
     * other
     */
    private String getRandomCode() {
        boolean useLetters = true;
        boolean useNumbers = true;
        int randomStrLen = 8;
        return RandomStringUtils.random(randomStrLen, useLetters, useNumbers);
    }
}