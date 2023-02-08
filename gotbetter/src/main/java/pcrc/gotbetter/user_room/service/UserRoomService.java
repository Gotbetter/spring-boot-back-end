package pcrc.gotbetter.user_room.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.service.UserReadUseCase;
import pcrc.gotbetter.user_room.data_access.entity.UserRoom;
import pcrc.gotbetter.user_room.data_access.repository.UserRoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class UserRoomService implements UserRoomOperationUseCase, UserRoomReadUseCase {
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public UserRoomService(UserRoomRepository userRoomRepository, RoomRepository roomRepository) {
        this.userRoomRepository = userRoomRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code) {
        Room room = roomRepository.findByRoomCode(room_code)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });

        Long user_id = validateJoinRoomRequest(room);
        UserRoom savedUserRoom = UserRoom.builder()
                .userId(user_id)
                .roomId(room.getRoomId())
                .refund(0)
                .accepted(false)
                .build();
        userRoomRepository.save(savedUserRoom);

        return RoomReadUseCase.FindRoomResult.builder()
                .room_id(room.getRoomId())
                .entry_fee(room.getEntryFee())
                .account(room.getAccount())
                .build();
    }

    @Override
    public List<UserReadUseCase.FindUserResult> getMemberListInARoom(Long room_id, Boolean accepted) {
        long user_id = validateLeaderIdOfRoom(room_id, accepted);
        List<User> users = userRoomRepository.findMembersInARoom(room_id, accepted);
        List<UserReadUseCase.FindUserResult> result = new ArrayList<>();

        for (User u : users) {
            if (accepted && (u.getId() == user_id)) {
                continue;
            }
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

    @Override
    public UserReadUseCase.FindUserResult approveJoinRoom(UserRoomAcceptedUpdateCommand command) {
        validateLeaderIdOfRoom(command.getRoom_id(), false);
        validateRequestUser(command);

        userRoomRepository.updateUserRoomAccepted(command.getRoom_id(), command.getId());

        List<User> users = userRoomRepository.findMembersInARoom(command.getRoom_id(), true);
        UserReadUseCase.FindUserResult result = null;

        for (User u : users) {
            if (Objects.equals(u.getId(), command.getId())) {
                result = UserReadUseCase.FindUserResult.builder()
                        .id(u.getId())
                        .auth_id(u.getAuthId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .build();
                break;
            }
        }
        return result;
    }

    /**
     * validate section
     */
    private Long validateJoinRoomRequest(Room room) {
        List<UserRoom> userRoomList = userRoomRepository.findByRoomId(room.getRoomId());
        Long user_id = getCurrentUserId();

        for (UserRoom ur : userRoomList) {
            if (Objects.equals(ur.getUserId(), user_id)) {
                throw new GotBetterException(MessageType.CONFLICT);
            }
        }
        if (room.getCurrentUserNum() >= room.getMaxUserNum()) {
            throw new GotBetterException(MessageType.NotAcceptable);
        }
        return user_id;
    }

    private Long validateLeaderIdOfRoom(Long room_id, Boolean accepted) {
        long user_id = getCurrentUserId();
        if (accepted && !userRoomRepository.existsMemberInARoom(room_id, user_id, true)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (!accepted && !userRoomRepository.existsRoomMatchLeaderId(user_id, room_id)) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        return user_id;
    }

    private void validateRequestUser(UserRoomAcceptedUpdateCommand command) {
        if (!userRoomRepository.existsMemberInARoom(command.getRoom_id(), command.getId(), false)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
    }
}
