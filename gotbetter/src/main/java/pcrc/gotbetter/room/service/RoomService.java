package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.entity.UserRoom;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.room.data_access.repository.UserRoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.domain.User;
import pcrc.gotbetter.user.service.UserReadUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        if (command.getStart_date().compareTo(command.getTarget_date()) >= 0) {
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }

        Room room = Room.builder()
                .title(command.getTitle())
                .maxUserNum(command.getMax_user_num())
                .startDate(command.getStart_date())
                .targetDate(command.getTarget_date())
                .entryFee(command.getEntry_fee())
                .roomCode(getRandomCode())
                .leaderId(user_id)
                .account(command.getAccount())
                .totalEntryFee(0)
                .ruleId(command.getRule_id())
                .build();
        roomRepository.save(room);

        UserRoom userRoom = UserRoom.builder()
                .userId(user_id)
                .roomId(room.getRoomId())
                .accepted(true)
                .build();
        userRoomRepository.save(userRoom);

        return FindRoomResult.findByRoom(room);
    }

    @Override
    public FindRoomResult requestJoinRoom(String room_code) {
        Room room = roomRepository.findByRoomCode(room_code)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });

        Long user_id = validateJoinRoomRequest(room);
        UserRoom savedUserRoom = UserRoom.builder()
                .userId(user_id)
                .roomId(room.getRoomId())
                .accepted(false)
                .build();
        userRoomRepository.save(savedUserRoom);

        return FindRoomResult.builder()
                .room_id(room.getRoomId())
                .entry_fee(room.getEntryFee())
                .account(room.getAccount())
                .build();
    }

    @Override
    public List<UserReadUseCase.FindUserResult> getWaitListForApprove(Long room_id) {
        validateLeaderIdOfRoom(room_id);

        List<User> users = roomRepository.findWaitUsersByRoomId(room_id);
        List<UserReadUseCase.FindUserResult> result = new ArrayList<>();
        for (User u : users) {
            result.add(UserReadUseCase.FindUserResult.builder()
                    .id(u.getId())
                    .auth_id(u.getAuthId())
                    .username(u.getUsernameNick())
                    .email(u.getEmail())
                    .profile(u.getProfile())
                    .build());
        }
        return result;
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

    /**
     * validate section
     */
    private void validateLeaderIdOfRoom(Long room_id) {
        Long user_id = getCurrentUserId();
        if (!roomRepository.existsRoomMatchLeaderId(user_id, room_id)) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }

    private Long validateJoinRoomRequest(Room room) {
        List<UserRoom> userRoomList = userRoomRepository.findByRoomId(room.getRoomId());
        Long user_id = getCurrentUserId();
        long count = 0L;

        for (UserRoom ur : userRoomList) {
            if (ur.getAccepted()) {
                count++;
            }
        }
        if (count >= room.getMaxUserNum()) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        for (UserRoom ur : userRoomList) {
            if (Objects.equals(ur.getUserId(), user_id)) {
                throw new GotBetterException(MessageType.CONFLICT);
            }
        }
        return user_id;
    }
}