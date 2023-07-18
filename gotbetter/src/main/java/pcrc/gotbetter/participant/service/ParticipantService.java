package pcrc.gotbetter.participant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.JoinRequestRepository;
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
    private final JoinRequestRepository joinRequestRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository,
                              JoinRequestRepository joinRequestRepository,
                              RoomRepository roomRepository) {
        this.participantRepository = participantRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public RoomReadUseCase.FindRoomResult requestJoinRoom(String roomCode) { // 방 입장 (멤버x)
        Room room = validateRoomWithRoomCode(roomCode); // 방 코드에 해당하는 방이 있는지 확인
        Long currentUserId = validateAbleToJoinRoom(room); // 해당 방에 사용자가 입장할 수 있는지 확인

        // 승인 요청
        JoinRequest joinRequest = JoinRequest.builder()
                .joinRequestId(JoinRequestId.builder()
                        .userId(currentUserId)
                        .roomId(room.getRoomId())
                        .build())
                .accepted(false)
                .build();
        joinRequestRepository.save(joinRequest);

        return RoomReadUseCase.FindRoomResult.builder()
                .roomId(room.getRoomId())
                .entryFee(room.getEntryFee())
                .account(room.getAccount())
                .build();
    }

    @Override
    public List<FindParticipantResult> getMemberListInARoom(Long roomId, Boolean accepted) {
        List<FindParticipantResult> result = new ArrayList<>();

        if (accepted) { // (방장 포함 일반 멤버) 방에 속한 멤버들 조회
            validateUserInRoom(roomId, false); // 방에 속한 멤버인지 검증
            List<ParticipantDto> participantDtoList = participantRepository.findUserInfoList(roomId);
            for (ParticipantDto p : participantDtoList) {
                result.add(FindParticipantResult.findByParticipant(p));
            }
        } else { // (방장만) 승인 대기 중인 사용자 조회
            validateUserInRoom(roomId, true); // 방장인지 검증
            List<JoinRequestDto> joinRequestList = joinRequestRepository.findJoinRequestList(null, roomId, false);
            for (JoinRequestDto joinRequest : joinRequestList) {
                result.add(FindParticipantResult.findByParticipant(joinRequest, -1L, false));
            }
        }
        return result;
    }

    @Override
    public FindParticipantResult approveJoinRoom(UserRoomAcceptedUpdateCommand command) { // (방장) 방 입장 승인
        validateUserInRoom(command.getRoomId(), true); // 방장인지 검증
        // 승인하려는 사용자 정보
        JoinRequestDto joinRequestDto = joinRequestRepository.findJoinRequest(
            command.getUserId(), command.getRoomId(), false);

        if (joinRequestDto == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        JoinRequest joinRequestInfo = joinRequestDto.getJoinRequest();
        Room roomInfo = joinRequestDto.getRoom();

        // 방의 인원이 만원인지 확인
        if (Objects.equals(roomInfo.getMaxUserNum(), roomInfo.getCurrentUserNum())) {
            throw new GotBetterException(MessageType.CONFLICT_MAX);
        }
        // 방 승인
        Participant participant = Participant.builder()
            .userId(joinRequestInfo.getJoinRequestId().getUserId())
            .roomId(joinRequestInfo.getJoinRequestId().getRoomId())
            .authority(false)
            .refund(0)
            .build();
        participantRepository.save(participant);
        // 사용자 방 요청을 수락했으므로 accepted를 true로 변경
        joinRequestInfo.updateAcceptedToJoin();
        joinRequestRepository.save(joinRequestInfo);
        // 방의 전체 입장비와 인원수 변경
        roomInfo.updateTotalEntryFeeAndCurrentUserNum(roomInfo.getEntryFee());
        roomRepository.save(roomInfo);
        // 프로필 수정 추가해야함.
        return FindParticipantResult.findByParticipant(joinRequestDto,
            participant.getParticipantId(), null);
    }

    @Override
    public Integer getMyRefund(Long participantId) { // 마지막 주차 끝난 후 조회 가능
        ParticipantDto participantDto = participantRepository.findParticipantRoom(participantId);

        // 방의 멤버인지 확인
        if (participantDto == null || !Objects.equals(participantDto.getParticipant().getUserId(), getCurrentUserId())) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        Participant participant = participantDto.getParticipant();
        Room room = participantDto.getRoom();

        // 마지막 주차가 종료됐는지 확인
        if (!Objects.equals(room.getWeek(), room.getCurrentWeek())) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        } else {
            LocalDate now = LocalDate.now();
            LocalDate lastDate = room.getStartDate().plusDays(7L * room.getCurrentWeek() - 1);
            if (!now.isAfter(lastDate)) {
                throw new GotBetterException(MessageType.FORBIDDEN_DATE);
            }
        }
        return participant.getRefund();
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
        Optional<JoinRequest> participate = joinRequestRepository.findByJoinRequestId(
                JoinRequestId.builder()
                        .userId(currentUserId)
                        .roomId(room.getRoomId()).build()
        );

        if (participate.isPresent()) {
            if (participate.get().getAccepted()) { // 이미 방에 참여했는지 확인
                throw new GotBetterException(MessageType.CONFLICT_JOIN);
            }
            else { // 아직 방에 참여 승인 받지 않았지만 승인 요청을 보내놓은 상태
                throw new GotBetterException(MessageType.CONFLICT);
            }
        }
        // 방에 참여하지 않았고, 승인 요청도 보내놓은 상태가 아닌 경우
        // 방의 인원이 만원인지 확인
        if (room.getCurrentUserNum() >= room.getMaxUserNum()) {
            throw new GotBetterException(MessageType.CONFLICT_MAX);
        }
        return currentUserId;
    }

    private void validateUserInRoom(Long roomId, Boolean needLeader) {
        long currentUserId = getCurrentUserId();
        Participant participant = participantRepository.findByUserIdAndRoomId(currentUserId, roomId);

        if (participant == null) { // 사용자가 방에 속해 있지 않은 경우 (오류)
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (needLeader && !participant.getAuthority()) { // 방장의 권한이 필요하지만 해당 방의 방장이 아닌 경우 (오류)
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }
}
