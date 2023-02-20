package pcrc.gotbetter.participant.service;

import com.querydsl.core.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.Participate;
import pcrc.gotbetter.participant.data_access.entity.ParticipateId;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.ParticipateRepository;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pcrc.gotbetter.participant.data_access.entity.QParticipant.participant;
import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;
import static pcrc.gotbetter.user.data_access.entity.QUser.user;

@Service
public class ParticipantService implements ParticipantOperationUseCase, ParticipantReadUseCase {
    private final ParticipantRepository participantRepository;
    private final ParticipateRepository participateRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository, ParticipateRepository participateRepository,
                              RoomRepository roomRepository, UserRepository userRepository) {
        this.participantRepository = participantRepository;
        this.participateRepository = participateRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code) {
        Room room = validateRoomWithRoomCode(room_code);
        Long user_id = validateAbleJoinRoom(room);

        Participate participate = Participate.builder()
                .participateId(ParticipateId.builder()
                        .userId(user_id)
                        .roomId(room.getRoomId())
                        .build())
                .accepted(false)
                .build();
        participateRepository.save(participate);

        return RoomReadUseCase.FindRoomResult.builder()
                .room_id(room.getRoomId())
                .entry_fee(room.getEntryFee())
                .account(room.getAccount())
                .build();
    }

    @Override
    public List<FindParticipantResult> getMemberListInARoom(Long room_id, Boolean accepted) {
        validateLeaderIdOfRoom(room_id, accepted);

        List<FindParticipantResult> result = new ArrayList<>();
        if (accepted) {
            List<Tuple> users = participantRepository.findActiveMembers(room_id);
            for (Tuple t : users) {
                result.add(FindParticipantResult.builder()
                        .participant_id(t.get(participant.participantId))
                        .user_id(t.get(user.userId))
                        .auth_id(t.get(user.authId))
                        .username(t.get(user.usernameNick))
                        .email(t.get(user.email))
                        .profile(t.get(user.profile))
                        .build());
            }
        } else {
            List<User> users = participantRepository.findWaitMembers(room_id);
            for (User u : users) {
                result.add(FindParticipantResult.builder()
                        .participant_id(-1L)
                        .user_id(u.getUserId())
                        .auth_id(u.getAuthId())
                        .username(u.getUsernameNick())
                        .email(u.getEmail())
                        .profile(u.getProfile())
                        .build());
            }
        }
        return result;
    }

    @Override
    public FindParticipantResult approveJoinRoom(UserRoomAcceptedUpdateCommand command) {
        Room room = validateRoomWithRoomId(command.getRoom_id());
        User user = validateUser(command.getUser_id());

        validateLeaderIdOfRoom(room.getRoomId(), false);
        validateRequestUser(command);

        Participant participant = Participant.builder()
                .userId(user.getUserId())
                .roomId(room.getRoomId())
                .authority(false)
                .refund(room.getEntryFee())
                .build();
        participantRepository.save(participant);
        participantRepository.updateParticipateAccepted(user.getUserId(), room.getRoomId());
        roomRepository.updatePlusTotalEntryFeeAndCurrentNum(command.getRoom_id(), room.getEntryFee());

        return FindParticipantResult.builder()
                .participant_id(participant.getParticipantId())
                .user_id(command.getUser_id())
                .auth_id(user.getAuthId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(user.getProfile())
                .build();
    }

    /**
     * validate section
     */
    private Room validateRoomWithRoomId(Long room_id) {
        return roomRepository.findByRoomId(room_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Room validateRoomWithRoomCode(String room_code) {
        return roomRepository.findByRoomCode(room_code).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private User validateUser(Long user_id) {
        return userRepository.findByUserId(user_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Long validateAbleJoinRoom(Room room) {
        Long user_id = getCurrentUserId();
        Optional<Participate> participate = participateRepository.findByParticipateId(
                ParticipateId.builder()
                        .userId(user_id)
                        .roomId(room.getRoomId()).build()
        );

        if (participate.isPresent()) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        if (room.getCurrentUserNum() >= room.getMaxUserNum()) {
            throw new GotBetterException(MessageType.NotAcceptable);
        }
        return user_id;
    }

    private void validateLeaderIdOfRoom(Long room_id, Boolean accepted) {
        long user_id = getCurrentUserId();
        Participant participant = participantRepository.findByUserIdAndRoomId(user_id, room_id);
        if (accepted && participant == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (!accepted && (participant == null || !participant.getAuthority())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }

    private void validateRequestUser(UserRoomAcceptedUpdateCommand command) {
        if (!participantRepository.existsWaitMemberInARoom(command.getUser_id(), command.getRoom_id(), false)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
    }
}
