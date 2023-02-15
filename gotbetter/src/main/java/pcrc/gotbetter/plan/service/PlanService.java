package pcrc.gotbetter.plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class PlanService implements PlanOperationUseCase, PlanReadUseCase {
    private final PlanRepository planRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    @Autowired
    public PlanService(PlanRepository planRepository,
                       RoomRepository roomRepository, ParticipantRepository participantRepository) {
        this.planRepository = planRepository;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public List<FindPlanResult> createPlans(PlanCreateCommand command) {
        Participant participant = participantRepository.findByParticipantId(command.getParticipant_id())
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        Room room = roomRepository.findByRoomId(participant.getRoomId())
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        validateLeaderIdOfRoom(participant.getRoomId());
        validateDuplicateCreatePlans(command.getParticipant_id());

        List<Plan> plans = new ArrayList<>();
        for (int i = 1;i <= room.getWeek();i++) {
            Plan plan = Plan.builder()
                    .participantId(command.getParticipant_id())
                    .userId(participant.getUserId())
                    .roomId(participant.getRoomId())
                    .startDate(room.getStartDate().plusDays((i - 1) * 7L))
                    .targetDate(room.getStartDate().plusDays(i * 7L - 1))
                    .score(0.0F)
                    .week(i)
                    .threeDaysPassed(false)
                    .rejected(false)
                    .build();
            plans.add(plan);
        }
        List<Plan> planList = planRepository.saveAll(plans);
        List<FindPlanResult> results = new ArrayList<>();
        for (Plan plan : planList) {
            results.add(FindPlanResult.findByPlan(plan));
        }
        return results;
    }

    @Override
    public FindPlanResult getWeekPlan(PlanFindQuery query) {
        Participant participant = participantRepository.findByParticipantId(query.getParticipant_id())
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        validateActiveUserInRoom(getCurrentUserId(), participant.getRoomId());

        Plan plan = planRepository.findWeekPlanOfUser(query.getParticipant_id(), query.getWeek()).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
        return FindPlanResult.findByPlan(plan);
    }

    /**
     * validate
     */
    private void validateLeaderIdOfRoom(Long room_id) {
        long user_id = getCurrentUserId();
        if (!participantRepository.isMatchedLeader(user_id, room_id)) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }

    private void validateActiveUserInRoom(Long user_id, Long room_id) {
        if (!participantRepository.existsMemberInRoom(user_id, room_id)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
    }

    private void validateDuplicateCreatePlans(Long participant_id) {
        if (planRepository.existsByParticipantId(participant_id)) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
    }
}
