package pcrc.gotbetter.participant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.Participate;
import pcrc.gotbetter.participant.data_access.entity.ParticipateId;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.ParticipateRepository;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.user.data_access.repository.UserSetRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class ParticipantService implements ParticipantOperationUseCase, ParticipantReadUseCase {
    private final ParticipantRepository participantRepository;
    private final ParticipateRepository participateRepository;
    private final RoomRepository roomRepository;
    private final ViewRepository viewRepository;
    private final UserSetRepository userSetRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository,
                              ParticipateRepository participateRepository,
                              RoomRepository roomRepository, ViewRepository viewRepository,
                              UserSetRepository userSetRepository) {
        this.participantRepository = participantRepository;
        this.participateRepository = participateRepository;
        this.roomRepository = roomRepository;
        this.viewRepository = viewRepository;
        this.userSetRepository = userSetRepository;
    }

    @Override
    public RoomReadUseCase.FindRoomResult requestJoinRoom(String roomCode) {
        Room room = validateRoomWithRoomCode(roomCode);
        Long currentUserId = validateAbleToJoinRoom(room);

        Participate participate = Participate.builder()
                .participateId(ParticipateId.builder()
                        .userId(currentUserId)
                        .roomId(room.getRoomId())
                        .build())
                .accepted(false)
                .build();
        participateRepository.save(participate);

        return RoomReadUseCase.FindRoomResult.builder()
                .roomId(room.getRoomId())
                .entryFee(room.getEntryFee())
                .account(room.getAccount())
                .build();
    }

    @Override
    public List<FindParticipantResult> getMemberListInARoom(Long roomId, Boolean accepted) {
        List<FindParticipantResult> result = new ArrayList<>();

        if (accepted) {
            validateUserInRoom(roomId, false);
            List<EnteredView> enteredViewList = viewRepository.enteredListByRoomId(roomId);
            for (EnteredView p : enteredViewList) {
                String authId = validateUserSetAuthId(p.getUserId());
                result.add(FindParticipantResult.findByParticipant(p, authId));
            }
        } else {
            validateUserInRoom(roomId, true);
            List<TryEnterView> tryEnterViewList = viewRepository
                    .tryEnterListByUserIdRoomId(null, roomId, false);
            for (TryEnterView p : tryEnterViewList) {
                String authId = validateUserSetAuthId(p.getTryEnterId().getUserId());
                result.add(FindParticipantResult.findByParticipant(p, -1L, false, authId));
            }
        }
        return result;
    }

    @Override
    public FindParticipantResult approveJoinRoom(UserRoomAcceptedUpdateCommand command) {
        validateUserInRoom(command.getRoomId(), true);

        TryEnterView targetUserInfo = viewRepository.tryEnterByUserIdRoomId(
                command.getUserId(), command.getRoomId(), false);

        if (targetUserInfo == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        // 방 인원 수 체크 추가
        if (Objects.equals(targetUserInfo.getMaxUserNum(), targetUserInfo.getCurrentUserNum())) {
            throw new GotBetterException(MessageType.CONFLICT_MAX);
        }
        Participant participant = Participant.builder()
                .userId(targetUserInfo.getTryEnterId().getUserId())
                .roomId(targetUserInfo.getTryEnterId().getRoomId())
                .authority(false)
                .refund(0)
                .build();
        participantRepository.save(participant);
        participantRepository.updateParticipateAccepted(targetUserInfo.getTryEnterId().getUserId(), targetUserInfo.getTryEnterId().getRoomId());
        roomRepository.updatePlusTotalEntryFeeAndCurrentNum(command.getRoomId(), targetUserInfo.getEntryFee());
        String authId = validateUserSetAuthId(targetUserInfo.getTryEnterId().getUserId());
        return FindParticipantResult.findByParticipant(targetUserInfo,
                participant.getParticipantId(), null, authId);
    }

    @Override
    public Integer getMyRefund(Long participantId) {
        EnteredView enteredView = viewRepository.enteredByParticipantId(participantId);

        if (enteredView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (!Objects.equals(enteredView.getWeek(), enteredView.getCurrentWeek())) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        } else {
            LocalDate now = LocalDate.now();
            LocalDate lastDate = enteredView.getStartDate().plusDays(7L * enteredView.getCurrentWeek() - 1);
            if (!now.isAfter(lastDate)) {
                throw new GotBetterException(MessageType.FORBIDDEN_DATE);
            }
        }

        return enteredView.getRefund();
    }

    /**
     * validate section
     */
    private Room validateRoomWithRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Long validateAbleToJoinRoom(Room room) {
        Long currentUserId = getCurrentUserId();
        Optional<Participate> participate = participateRepository.findByParticipateId(
                ParticipateId.builder()
                        .userId(currentUserId)
                        .roomId(room.getRoomId()).build()
        );

        if (participate.isPresent()) { // 이미 방에 참여했는지 승인요청만보냈 상태인지 분리
            if (participate.get().getAccepted()) {
                throw new GotBetterException(MessageType.CONFLICT_JOIN);
            }
            else {
                throw new GotBetterException(MessageType.CONFLICT);
            }
        }
        if (room.getCurrentUserNum() >= room.getMaxUserNum()) {
            throw new GotBetterException(MessageType.CONFLICT_MAX);
        }
        return currentUserId;
    }

    private void validateUserInRoom(Long roomId, Boolean needLeader) {
        long currentUserId = getCurrentUserId();
        EnteredView enteredView = viewRepository.enteredByUserIdRoomId(currentUserId, roomId);

        if (enteredView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (needLeader && !enteredView.getAuthority()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }

    private String validateUserSetAuthId(Long userId) {
        String authId = userSetRepository.findAuthIdByUserId(userId);
        if (authId == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return authId;
    }
}
