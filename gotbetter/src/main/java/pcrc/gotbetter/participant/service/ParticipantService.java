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

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository,
                              ParticipateRepository participateRepository,
                              RoomRepository roomRepository, ViewRepository viewRepository) {
        this.participantRepository = participantRepository;
        this.participateRepository = participateRepository;
        this.roomRepository = roomRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public RoomReadUseCase.FindRoomResult requestJoinRoom(String room_code) {
        Room room = validateRoomWithRoomCode(room_code);
        Long user_id = validateAbleToJoinRoom(room);

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
        List<FindParticipantResult> result = new ArrayList<>();

        if (accepted) {
            validateUserInRoom(room_id, false);
            List<EnteredView> enteredViewList = viewRepository.enteredListByRoomId(room_id);
            for (EnteredView p : enteredViewList) {
                result.add(FindParticipantResult.findByParticipant(p));
            }
        } else {
            validateUserInRoom(room_id, true);
            List<TryEnterView> tryEnterViewList = viewRepository
                    .tryEnterListByUserIdRoomId(null, room_id, false);
            for (TryEnterView p : tryEnterViewList) {
                result.add(FindParticipantResult.findByParticipant(p, -1L, false));
            }
        }
        return result;
    }

    @Override
    public FindParticipantResult approveJoinRoom(UserRoomAcceptedUpdateCommand command) {
        validateUserInRoom(command.getRoom_id(), true);

        TryEnterView targetUserInfo = viewRepository.tryEnterByUserIdRoomId(
                command.getUser_id(), command.getRoom_id(), false);

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
        roomRepository.updatePlusTotalEntryFeeAndCurrentNum(command.getRoom_id(), targetUserInfo.getEntryFee());
        return FindParticipantResult.findByParticipant(targetUserInfo,
                participant.getParticipantId(), null);
    }

    @Override
    public Integer getMyRefund(Long participant_id) {
        EnteredView enteredView = viewRepository.enteredByParticipantId(participant_id);

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
    private Room validateRoomWithRoomCode(String room_code) {
        return roomRepository.findByRoomCode(room_code).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Long validateAbleToJoinRoom(Room room) {
        Long user_id = getCurrentUserId();
        Optional<Participate> participate = participateRepository.findByParticipateId(
                ParticipateId.builder()
                        .userId(user_id)
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
        return user_id;
    }

    private void validateUserInRoom(Long room_id, Boolean need_leader) {
        long user_id = getCurrentUserId();
        EnteredView enteredView = viewRepository.enteredByUserIdRoomId(user_id, room_id);

        if (enteredView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (need_leader && !enteredView.getAuthority()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }
}
