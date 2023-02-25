package pcrc.gotbetter.plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class PlanService implements PlanOperationUseCase, PlanReadUseCase {
    private final PlanRepository planRepository;
    private final ParticipantRepository participantRepository;
    private final ViewRepository viewRepository;

    @Autowired
    public PlanService(PlanRepository planRepository,
                       ParticipantRepository participantRepository,
                       ViewRepository viewRepository) {
        this.planRepository = planRepository;
        this.participantRepository = participantRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public List<FindPlanResult> createPlans(PlanCreateCommand command) {
        EnteredView enteredView = validateEnteredView(command.getParticipant_id());

        validateSenderIsLeader(enteredView.getRoomId());
        validateDuplicateCreatePlans(command.getParticipant_id());

        List<Plan> plans = new ArrayList<>();
        for (int i = 1;i <= enteredView.getWeek();i++) {
            Plan plan = Plan.builder()
                    .participantInfo(ParticipantInfo.builder()
                            .participantId(enteredView.getParticipantId())
                            .userId(enteredView.getUserId())
                            .roomId(enteredView.getRoomId())
                            .build())
                    .startDate(enteredView.getStartDate().plusDays((i - 1) * 7L))
                    .targetDate(enteredView.getStartDate().plusDays(i * 7L - 1))
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
        EnteredView enteredView = validateEnteredView(query.getParticipant_id());
        validateSenderInRoom(enteredView.getRoomId());

        Plan plan = planRepository.findWeekPlanOfUser(query.getParticipant_id(), query.getWeek());
        if (plan == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return FindPlanResult.findByPlan(plan);
    }

    /**
     * validate
     */
    private EnteredView validateEnteredView(Long participant_id) {
        EnteredView enteredView = viewRepository.enteredByParticipantId(participant_id);

        if (enteredView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return enteredView;
    }

    private void validateSenderIsLeader(Long room_id) {
        long user_id = getCurrentUserId();
        if (!participantRepository.isMatchedLeader(user_id, room_id)) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }

    private void validateDuplicateCreatePlans(Long participant_id) {
        if (planRepository.existsByParticipantId(participant_id)) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
    }

    private void validateSenderInRoom(Long room_id) {
        long user_id = getCurrentUserId();
        if (!viewRepository.enteredExistByUserIdRoomId(user_id, room_id)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
    }
}
