package pcrc.gotbetter.plan_evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluationId;
import pcrc.gotbetter.plan_evaluation.data_access.repository.PlanEvaluationRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class PlanEvaluationService implements PlanEvaluationOperationUseCase,  PlanEvaluationReadUseCase {
    private final PlanEvaluationRepository planEvaluationRepository;
    private final PlanRepository planRepository;
    private final ParticipantRepository participantRepository;
    private final RoomRepository roomRepository;
    private final DetailPlanRepository detailPlanRepository;

    @Autowired
    public PlanEvaluationService(PlanEvaluationRepository planEvaluationRepository, PlanRepository planRepository,
                                 ParticipantRepository participantRepository, RoomRepository roomRepository,
                                 DetailPlanRepository detailPlanRepository) {
        this.planEvaluationRepository = planEvaluationRepository;
        this.planRepository = planRepository;
        this.participantRepository = participantRepository;
        this.roomRepository = roomRepository;
        this.detailPlanRepository = detailPlanRepository;
    }

    @Override
    @Transactional
    public void createPlanEvaluation(PlanEvaluationCommand command) {
        Plan plan = validatePlan(command.getPlan_id());
        Long user_id = getCurrentUserId();
        Participant participant = validateMemberInRoom(user_id, plan.getParticipantInfo().getRoomId());
        Room room = validateRoom(participant.getRoomId());

        if (!Objects.equals(room.getCurrentWeek(), plan.getWeek())
                || plan.getThreeDaysPassed()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (plan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (planEvaluationRepository.existsEval(plan.getPlanId(), participant.getParticipantId())) {
            throw new GotBetterException(MessageType.CONFLICT);
        }

        List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(command.getPlan_id());
        if (Math.ceil(room.getCurrentUserNum()) / 2 <= planEvaluations.size() + 1) {
            planRepository.updateRejected(plan.getPlanId(), true);
            planEvaluationRepository.deleteByPlanEvaluationIdPlanId(plan.getPlanId());
            detailPlanRepository.deleteByPlanId(plan.getPlanId());
        } else {
            PlanEvaluation planEvaluation = PlanEvaluation.builder()
                    .planEvaluationId(PlanEvaluationId.builder()
                            .planId(plan.getPlanId())
                            .participantId(participant.getParticipantId())
                            .userId(participant.getUserId())
                            .roomId(participant.getRoomId())
                            .build())
                    .build();
            planEvaluationRepository.save(planEvaluation);
        }
    }

    @Override
    public FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query) {
        Plan plan = validatePlan(query.getPlan_id());
        Participant participant = validateMemberInRoom(getCurrentUserId(), plan.getParticipantInfo().getRoomId());
        List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(query.getPlan_id());
        boolean checked = false;

        for (PlanEvaluation p : planEvaluations) {
            if (Objects.equals(p.getPlanEvaluationId().getUserId(), participant.getUserId())) {
                checked = true;
                break;
            }
        }

        return FindPlanEvaluationResult.findByPlanEvaluation(planEvaluations.size(), checked);
    }

    @Override
    public void deletePlanEvaluation(PlanEvaluationCommand command) {
        Plan plan = validatePlan(command.getPlan_id());
        Room room = validateRoom(plan.getParticipantInfo().getRoomId());
        Long user_id = getCurrentUserId();
        Participant participant = validateMemberInRoom(user_id, plan.getParticipantInfo().getRoomId());

        if (!Objects.equals(room.getCurrentWeek(), plan.getWeek())
                || plan.getThreeDaysPassed()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (planEvaluationRepository.existsEval(plan.getPlanId(), participant.getParticipantId())) {
            planEvaluationRepository.deletePlanEvaluation(plan.getPlanId(), participant.getParticipantId());
            return;
        }
        throw new GotBetterException(MessageType.NOT_FOUND);
    }

    /**
     * validate
     */
    private Plan validatePlan(Long plan_id) {
        return planRepository.findByPlanId(plan_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Room validateRoom(Long room_id) {
        return roomRepository.findByRoomId(room_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Participant validateMemberInRoom(Long user_id, Long room_id) {
        Participant participant = participantRepository.findByUserIdAndRoomId(user_id, room_id);
        if (participant == null) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        return participant;
    }
}
