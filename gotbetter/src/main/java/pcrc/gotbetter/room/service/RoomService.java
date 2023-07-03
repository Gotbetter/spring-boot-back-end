package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;
import pcrc.gotbetter.common.data_access.repository.CommonCodeRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.Participate;
import pcrc.gotbetter.participant.data_access.entity.ParticipateId;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.ParticipateRepository;
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
    private final ParticipateRepository participateRepository;
    private final ParticipantRepository participantRepository;
    private final ViewRepository viewRepository;
    private final CommonCodeRepository commonCodeRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, ParticipateRepository participateRepository,
                       ParticipantRepository participantRepository, ViewRepository viewRepository,
                       CommonCodeRepository commonCodeRepository) {
        this.roomRepository = roomRepository;
        this.participateRepository = participateRepository;
        this.participantRepository = participantRepository;
        this.viewRepository = viewRepository;
        this.commonCodeRepository = commonCodeRepository;
    }

    @Override
    public List<FindRoomResult> getUserRooms() {
        Long currentUserId = getCurrentUserId();
        List<FindRoomResult> result = new ArrayList<>();
        List<TryEnterView> tryEnterViewList = viewRepository
                .tryEnterListByUserIdRoomId(currentUserId, null, true);
        // room_category 리스트 가져오기
        List<CommonCode> roomCategories = commonCodeRepository.findRoomCategories();
        HashMap<String, CommonCode> roomCategoriesMap = new HashMap<>();
        for (CommonCode roomCategory : roomCategories) {
            roomCategoriesMap.put(roomCategory.getCommonCodeId().getCode(), roomCategory);
        }

        for (TryEnterView t : tryEnterViewList) {
            CommonCode roomCategoryInfo = roomCategoriesMap.get(t.getRoomCategory());
            result.add(FindRoomResult.findByRoom(t, roomCategoryInfo.getCodeDescription()));
        }
        return result;
    }

    @Override
    public FindRoomResult getOneRoomInfo(Long roomId) {
        Long currentUserId = getCurrentUserId();
        TryEnterView tryEnterView = viewRepository.tryEnterByUserIdRoomId(currentUserId, roomId, true);

        if (tryEnterView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        CommonCode roomCategoryInfo = findRoomCategoryInfo(tryEnterView.getRoomCategory());
        return FindRoomResult.findByRoom(tryEnterView, roomCategoryInfo.getCodeDescription());
    }

    @Override
    public FindRoomResult createRoom(RoomCreateCommand command) {
        Long currentUserId = getCurrentUserId();
        String roomCode = getRandomCode();
        LocalDate startDate = LocalDate.parse(command.getStartDate(), DateTimeFormatter.ISO_DATE);

        if (startDate.isBefore(LocalDate.now())) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        }

        // category
        CommonCode roomCategoryInfo = findRoomCategoryInfo(command.getRoomCategoryCode());

        // ruleId가 디비에 있는지 확인 - 일단은 이것만
        if (!commonCodeRepository.existsByCommonCodeId(CommonCodeId.builder()
                .groupCode("RULE").code(command.getRuleId()).build())) {
            System.out.println("22");
            throw new GotBetterException(MessageType.BAD_REQUEST);
        }

        // ruleId가 커스텀된 것일 때 적용

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
                .ruleId(command.getRuleId())
                .build();
        roomRepository.save(room);

        Participate participate = Participate.builder()
                .participateId(ParticipateId.builder()
                        .userId(currentUserId)
                        .roomId(room.getRoomId())
                        .build())
                .accepted(true)
                .build();
        participateRepository.save(participate);

        Participant participant = Participant.builder()
                .userId(participate.getParticipateId().getUserId())
                .roomId(participate.getParticipateId().getRoomId())
                .authority(true)
                .refund(0)
                .build();
        participantRepository.save(participant);

        return FindRoomResult.findByRoom(room, participant.getParticipantId(),
                roomCategoryInfo.getCodeDescription());
    }

    @Override
    public List<FindRankResult> getRank(Long roomId) {
        Long currentUserId = getCurrentUserId();
        if (!viewRepository.enteredExistByUserIdRoomId(currentUserId, roomId)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

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
        if (roomCategoryCode == null) {
            return commonCodeRepository.findRoomCategoryInfo("ETC");
        } else {
            CommonCode roomCategoryInfo = commonCodeRepository.findRoomCategoryInfo(roomCategoryCode);
            if (roomCategoryInfo == null) {
                throw new GotBetterException(MessageType.BAD_REQUEST);
            }
            return roomCategoryInfo;
        }
    }
}