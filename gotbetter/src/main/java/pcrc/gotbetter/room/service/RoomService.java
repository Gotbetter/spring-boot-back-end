package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;
import pcrc.gotbetter.common.data_access.repository.CommonCodeRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.JoinRequestRepository;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class RoomService implements RoomOperationUseCase, RoomReadUseCase {
    private final RoomRepository roomRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final ParticipantRepository participantRepository;
    private final CommonCodeRepository commonCodeRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository,
        JoinRequestRepository joinRequestRepository,
        ParticipantRepository participantRepository,
        CommonCodeRepository commonCodeRepository) {
        this.roomRepository = roomRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.participantRepository = participantRepository;
        this.commonCodeRepository = commonCodeRepository;
    }

    @Override
    public FindRoomResult createRoom(RoomCreateCommand command) {
        Long currentUserId = getCurrentUserId();
        // 방 코드 생성
        String roomCode = getRandomCode();
        // 방 계획 시작 날짜
        LocalDate startDate = LocalDate.parse(command.getStartDate(), DateTimeFormatter.ISO_DATE);

        // 방 계획 시작 날짜가 과거 날짜인지 확인
        if (startDate.isBefore(LocalDate.now())) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        }

        // category 정보
        CommonCode roomCategoryInfo = findRoomCategoryInfo(command.getRoomCategoryCode());

        // rule 정보 - 고정된 규칙
        CommonCode ruleInfo = findRuleInfo(command.getRuleCode());
        // rule 정보 - 커스텀

        // 방 데이터 insert
        Room room = Room.builder()
            .title(command.getTitle())
            .maxUserNum(command.getMaxUserNum())
            .currentUserNum(1)
            .startDate(startDate)
            .week(command.getWeek())
            .currentWeek(command.getCurrentWeek())
            .entryFee(command.getEntryFee())
            .roomCode(roomCode)
            .account(command.getAccount())
            .roomCategory(roomCategoryInfo.getCommonCodeId().getCode())
            .description(command.getDescription())
            .totalEntryFee(command.getEntryFee())
            .rule(ruleInfo.getCommonCodeId().getCode())
            .build();
        roomRepository.save(room);

        // join request 데이터 insert
        JoinRequest joinRequest = JoinRequest.builder()
            .joinRequestId(JoinRequestId.builder()
                .userId(currentUserId)
                .roomId(room.getRoomId())
                .build())
            .accepted(true)
            .build();
        joinRequestRepository.save(joinRequest);

        // participant 데이터 insert
        Participant participant = Participant.builder()
            .userId(joinRequest.getJoinRequestId().getUserId())
            .roomId(joinRequest.getJoinRequestId().getRoomId())
            .authority(true)
            .refund(0)
            .build();
        participantRepository.save(participant);

        return FindRoomResult.findByRoom(room, participant.getParticipantId(),
            roomCategoryInfo.getCodeDescription(),
            ruleInfo.getCodeDescription());
    }

    @Override
    public FindRoomResult updateRoom(RoomUpdateCommand command) {
        // 사용자가 방에 속해있는지 확인
        ParticipantDto participantDto = participantRepository.findParticipantByUserIdAndRoomId(getCurrentUserId(), command.getRoom_id());

        if (participantDto == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        // 방장이 맞는지 확인 - participant
        Participant participant = participantDto.getParticipant();

        if (!participant.getAuthority()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        // 방 소개 수정 - room
        Room room = participantDto.getRoom();

        room.updateDescription(command.getDescription());
        roomRepository.save(room);

        CommonCode roomCategoryInfo = findRoomCategoryInfo(room.getRoomCategory());
        CommonCode ruleInfo = findRuleInfo(room.getRule());

        return FindRoomResult.findByRoom(room, null,
            roomCategoryInfo.getCodeDescription(), ruleInfo.getCodeDescription());
    }

    @Override
    public List<FindRoomResult> getUserRoomList() {
        Long currentUserId = getCurrentUserId();
        List<FindRoomResult> result = new ArrayList<>();
        // 유저가 속한 방 리스트 - JoinRequest 대신 Participant에서 사용 가능 - room 정보
        List<JoinRequestDto> joinRequestDtoList = joinRequestRepository.findJoinRequestJoinList(currentUserId, null, true);
        // common code 모든 데이터 (ROOM_CATEGORY + RULE)
        List<CommonCode> commonCodes = commonCodeRepository.findListByGroupCode("");
        HashMap<String, CommonCode> commonCodeHashMap = new HashMap<>();

        for (CommonCode commonCode : commonCodes) {
            commonCodeHashMap.put(commonCode.getCommonCodeId().getGroupCode()
                    + "/" + commonCode.getCommonCodeId().getCode(), commonCode);
        }
        for (JoinRequestDto joinRequest : joinRequestDtoList) {
            CommonCode roomCategoryInfo = commonCodeHashMap.get("ROOM_CATEGORY/" + joinRequest.getRoom().getRoomCategory());
            CommonCode ruleInfo = commonCodeHashMap.get("RULE/" + joinRequest.getRoom().getRule());
            result.add(FindRoomResult.findByRoom(joinRequest, roomCategoryInfo.getCodeDescription(),
                    ruleInfo.getCodeDescription()));
        }
        return result;
    }

    @Override
    public FindRoomResult getOneRoomInfo(Long roomId) {
        Long currentUserId = getCurrentUserId();
        // 유저가 속한 방 정보
        JoinRequestDto joinRequestDto = joinRequestRepository.findJoinRequestJoin(currentUserId, roomId, true);

        if (joinRequestDto == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        // 카테고리 정보
        CommonCode roomCategoryInfo = findRoomCategoryInfo(joinRequestDto.getRoom().getRoomCategory());
        // rule 정보 - 고정된 규칙
        CommonCode ruleInfo = findRuleInfo(joinRequestDto.getRoom().getRule());
        return FindRoomResult.findByRoom(joinRequestDto,
            roomCategoryInfo.getCodeDescription(), ruleInfo.getCodeDescription());
    }

    @Override
    public List<FindRankResult> getRank(Long roomId) {
        Long currentUserId = getCurrentUserId();

        if (!participantRepository.existsByUserIdAndRoomId(currentUserId, roomId)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        List<ParticipantDto> participantDtoList = participantRepository.findParticipantRoomByRoomId(roomId);
        List<FindRankResult> findRankResultList = new ArrayList<>();
        LocalDate now = LocalDate.now();

        if (participantDtoList.size() == 0 ||
            now.isBefore(participantDtoList.get(0).getRoom().getStartDate())) {
            return findRankResultList;
        }

        Room room = participantDtoList.get(0).getRoom();
        Map<Float, List<String>> percentMap = new HashMap<>();

        for (ParticipantDto participantDto : participantDtoList) {
            User user = participantDto.getUser();
            Participant participant = participantDto.getParticipant();
            Float key = participant.getPercentSum();
            List<String> usernameList = new ArrayList<>();

            if (percentMap.containsKey(key)) {
                usernameList = percentMap.get(key);
            }
            usernameList.add(user.getUsername());
            percentMap.put(key, usernameList);
        }

        List<Float> keySet = new ArrayList<>(percentMap.keySet());

        Collections.reverse(keySet);

        int rank = 1;

        for (Float key : keySet) {
            List<String> usernames = percentMap.get(key);

            for (String username : usernames) {
                int refund = room.getEntryFee();

                if (key == 0F) {
                    rank = participantDtoList.size();
                    refund = 0;
                } else {
                    if (rank == 1) {
                        refund *= 2;
                    }
                }
                findRankResultList.add(FindRankResult
                    .findByRank(username, rank, refund));
            }
            rank += percentMap.get(key).size();
        }
        return findRankResultList;
    }

    /**
     * other
     */
    private String getRandomCode() {
        boolean useLetters = true;
        boolean useNumbers = true;
        int randomStrLen = 8;

        String roomCode;
        do {
            roomCode = RandomStringUtils.random(randomStrLen, useLetters, useNumbers);
        } while (roomRepository.existByRoomCode(roomCode));
        return roomCode;
    }

    private CommonCode findRoomCategoryInfo(String roomCategoryCode) {
        if (roomCategoryCode == null) { // 선택되지 않은 상태면 기본적으로 ETC로 설정
            roomCategoryCode = "ETC";
        }
        CommonCode roomCategoryInfo = commonCodeRepository.findByCommonCodeId(CommonCodeId.builder()
            .groupCode("ROOM_CATEGORY")
            .code(roomCategoryCode)
            .build());
        if (roomCategoryInfo == null) { // 카테고리 목록에 해당되는 것이 아니면 null 반환
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }
        return roomCategoryInfo;
    }

    private CommonCode findRuleInfo(String ruleCode) {
        if (ruleCode == null) {
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }
        CommonCode ruleInfo = commonCodeRepository.findByCommonCodeId(CommonCodeId.builder()
                .groupCode("RULE")
                .code(ruleCode)
                .build());
        if (ruleInfo == null) {
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }
        return ruleInfo;
    }
}