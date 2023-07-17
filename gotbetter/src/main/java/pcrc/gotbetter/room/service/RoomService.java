package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;
import pcrc.gotbetter.common.data_access.repository.CommonCodeRepository;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.JoinRequestRepository;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class RoomService implements RoomOperationUseCase, RoomReadUseCase {
    private final RoomRepository roomRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final ParticipantRepository participantRepository;
    private final ViewRepository viewRepository;
    private final CommonCodeRepository commonCodeRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, JoinRequestRepository joinRequestRepository,
        ParticipantRepository participantRepository, ViewRepository viewRepository,
        CommonCodeRepository commonCodeRepository) {
        this.roomRepository = roomRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.participantRepository = participantRepository;
        this.viewRepository = viewRepository;
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
    public List<FindRoomResult> getUserRoomList() {
        Long currentUserId = getCurrentUserId();
        List<FindRoomResult> result = new ArrayList<>();
        // 유저가 속한 방 리스트 - JoinRequest 대신 Participant에서 사용 가능 - room 정보
        List<JoinRequestDto> joinRequestDtoList = joinRequestRepository.findJoinRequestList(currentUserId, null, true);
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
        JoinRequestDto joinRequestDto = joinRequestRepository.findJoinRequest(currentUserId, roomId, true);

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
        if (!viewRepository.enteredExistByUserIdRoomId(currentUserId, roomId)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        // participant - room - user
        List<EnteredView> enteredViewList = viewRepository.enteredListByRoomId(roomId);
        List<FindRankResult> findRankResultList = new ArrayList<>();

        LocalDate now = LocalDate.now();
        if (now.isBefore(enteredViewList.get(0).getStartDate())) {
            return findRankResultList;
        }

        // percent sum 기준 정렬
        enteredViewList.sort((o1, o2) -> (int) (o2.getPercentSum() - o1.getPercentSum()));

        int rank = 1;
        for (EnteredView enteredView : enteredViewList) {
            int refund = enteredView.getEntryFee();
            if (rank == 1) {
                refund *= 2;
            } else if (enteredView.getCurrentUserNum() == rank) {
                refund = 0;
            }
            findRankResultList.add(FindRankResult.findByRank(
                    enteredView.getUsernameNick(), rank, refund));
            rank++;
        }
        return findRankResultList;
    }

//    @Override
//    public List<FindRankResult> getRank(Long room_id) {
//        Long user_id = getCurrentUserId();
//        if (!viewRepository.enteredExistByUserIdRoomId(user_id, room_id)) {
//            throw new GotBetterException(MessageType.NOT_FOUND);
//        }
//
//        List<EnteredView> enteredViewList = viewRepository.enteredListByRoomId(room_id);
//        List<FindRankResult> findRankResultList = new ArrayList<>();
//
//        // percent sum 기준 정렬
//        enteredViewList.sort((o1, o2) -> (int) (o2.getPercentSum() - o1.getPercentSum()));
//
//        int rank = 1;
//        float beforePercentSum = enteredViewList.get(1).getPercentSum();
//        for (EnteredView enteredView : enteredViewList) {
//            int refund = enteredView.getEntryFee();
//            int backupRank = rank;
//            if (findRankResultList.size() != 0 && enteredView.getPercentSum() == beforePercentSum) {
//                rank = findRankResultList.get(findRankResultList.size() - 1).getRank();
//            }
//            if (rank == 1) {
//                refund *= 2;
//            } else if (enteredView.getCurrentUserNum() == rank) {
//                refund = 0;
//            }
//            findRankResultList.add(FindRankResult.findByRank(
//                    enteredView.getUsernameNick(), rank, refund));
//            rank = backupRank;
//            rank++;
//            beforePercentSum = enteredView.getPercentSum();
//        }
//        return findRankResultList;
//    }

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