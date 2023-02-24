package pcrc.gotbetter.room.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.Participate;
import pcrc.gotbetter.participant.data_access.entity.ParticipateId;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.TryEnterView;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.participant.data_access.repository.ParticipateRepository;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.ArrayList;
import java.util.List;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class RoomService implements RoomOperationUseCase, RoomReadUseCase {
    private final RoomRepository roomRepository;
    private final ParticipateRepository participateRepository;
    private final ParticipantRepository participantRepository;
    private final ViewRepository viewRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, ParticipateRepository participateRepository,
                       ParticipantRepository participantRepository, ViewRepository viewRepository) {
        this.roomRepository = roomRepository;
        this.participateRepository = participateRepository;
        this.participantRepository = participantRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public List<FindRoomResult> getUserRooms() {
        Long user_id = getCurrentUserId();
        List<FindRoomResult> result = new ArrayList<>();
        List<TryEnterView> tryEnterViewList = viewRepository
                .tryEnterListByUserIdRoomId(user_id, null, true);

        for (TryEnterView t : tryEnterViewList) {
            result.add(FindRoomResult.builder()
                    .room_id(t.getRoomId())
                    .title(t.getTitle())
                    .build());
        }
        return result;
    }

    @Override
    public FindRoomResult getOneRoomInfo(Long room_id) {
        Long user_id = getCurrentUserId();
        TryEnterView tryEnterView = viewRepository.tryEnterByUserIdRoomId(user_id, room_id, true);

        if (tryEnterView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return FindRoomResult.findByRoom(tryEnterView);
    }

    @Override
    public FindRoomResult createRoom(RoomCreateCommand command) {
        Long user_id = getCurrentUserId();
        String room_code = getRandomCode();

        Room room = Room.builder()
                .title(command.getTitle())
                .maxUserNum(command.getMax_user_num())
                .currentUserNum(1)
                .startDate(command.getStart_date())
                .week(command.getWeek())
                .currentWeek(command.getCurrent_week())
                .entryFee(command.getEntry_fee())
                .roomCode(room_code)
                .account(command.getAccount())
                .totalEntryFee(command.getEntry_fee())
                .ruleId(command.getRule_id())
                .build();
        roomRepository.save(room);

        Participate participate = Participate.builder()
                .participateId(ParticipateId.builder()
                        .userId(user_id)
                        .roomId(room.getRoomId())
                        .build())
                .accepted(true)
                .build();
        participateRepository.save(participate);

        Participant participant = Participant.builder()
                .userId(participate.getParticipateId().getUserId())
                .roomId(participate.getParticipateId().getRoomId())
                .authority(true)
                .refund(room.getEntryFee())
                .build();
        participantRepository.save(participant);

        return FindRoomResult.findByRoom(room, participant.getParticipantId());
    }

    /**
     * other
     */
    private String getRandomCode() {
        boolean useLetters = true;
        boolean useNumbers = true;
        int randomStrLen = 8;

        String room_code;
        do {
            room_code = RandomStringUtils.random(randomStrLen, useLetters, useNumbers);
        } while (roomRepository.existByRoomCode(room_code));
        return room_code;
    }
}