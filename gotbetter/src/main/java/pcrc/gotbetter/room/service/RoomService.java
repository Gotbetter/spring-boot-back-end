package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.entity.UserRoom;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.room.data_access.repository.UserRoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.domain.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService implements RoomOperationUseCase, RoomReadUseCase {
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, UserRoomRepository userRoomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRoomRepository = userRoomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<FindRoomResult> getUserRooms() {
        Long user_id = validateUser();
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
        validateUserInRoom(room_id);
        Room room = roomRepository.findByRoomId(room_id)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        return FindRoomResult.findByRoom(room);
    }

    @Override
    public FindRoomResult createRoom(RoomCreateCommand command) {
        Long user_id = validateUser();

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
        Room saved = roomRepository.save(room);

        UserRoom userRoom = UserRoom.builder()
                .userId(user_id)
                .roomId(saved.getRoomId())
                .accepted(true)
                .build();
        userRoomRepository.save(userRoom);

        return FindRoomResult.findByRoom(saved);
    }

    private Long validateUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal.equals("anonymousUser")) {
            throw new GotBetterException(MessageType.ReLogin);
        }

        UserDetails userDetails = (UserDetails) principal;
        User user = userRepository.findByAuthId(userDetails.getUsername())
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        return user.getId();
    }

    private void validateUserInRoom(Long room_id) {
        Long user_id = validateUser();
        if (!roomRepository.existsByUserIdAndRoomId(user_id, room_id)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
    }

    private String getRandomCode() {
        boolean useLetters = true;
        boolean useNumbers = true;
        int randomStrLen = 8;
        return RandomStringUtils.random(randomStrLen, useLetters, useNumbers);
    }
}