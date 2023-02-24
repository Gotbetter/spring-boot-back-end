package pcrc.gotbetter.plan_evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
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
    private final DetailPlanRepository detailPlanRepository;
    private final ViewRepository viewRepository;

    @Autowired
    public PlanEvaluationService(PlanEvaluationRepository planEvaluationRepository, PlanRepository planRepository,
                                 DetailPlanRepository detailPlanRepository, ViewRepository viewRepository) {
        this.planEvaluationRepository = planEvaluationRepository;
        this.planRepository = planRepository;
        this.detailPlanRepository = detailPlanRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    @Transactional
    public void createPlanEvaluation(PlanEvaluationCommand command) {
        //detail plan이 아무것도 없으면 막기
        Plan plan = validatePlan(command.getPlan_id());
        EnteredView enteredView = validateEnteredView(plan.getParticipantInfo().getRoomId());

        if (!Objects.equals(enteredView.getCurrentWeek(), plan.getWeek())
                || plan.getThreeDaysPassed()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (plan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (planEvaluationRepository.existsEval(plan.getPlanId(), enteredView.getParticipantId())) {
            throw new GotBetterException(MessageType.CONFLICT);
        }

        List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(command.getPlan_id());
        if (Math.ceil(enteredView.getCurrentUserNum()) / 2 <= planEvaluations.size() + 1) {
            planRepository.updateRejected(plan.getPlanId(), true);
            planEvaluationRepository.deleteByPlanEvaluationIdPlanId(plan.getPlanId());
            detailPlanRepository.deleteByPlanId(plan.getPlanId());
        } else {
            PlanEvaluation planEvaluation = PlanEvaluation.builder()
                    .planEvaluationId(PlanEvaluationId.builder()
                            .planId(plan.getPlanId())
                            .participantId(enteredView.getParticipantId())
                            .userId(enteredView.getUserId())
                            .roomId(enteredView.getRoomId())
                            .build())
                    .build();
            planEvaluationRepository.save(planEvaluation);
        }
    }

    @Override
    public FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query) {
        Plan plan = validatePlan(query.getPlan_id());
        EnteredView enteredView = validateEnteredView(plan.getParticipantInfo().getRoomId());
        List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(query.getPlan_id());
        boolean checked = false;

        for (PlanEvaluation p : planEvaluations) {
            if (Objects.equals(p.getPlanEvaluationId().getUserId(), enteredView.getUserId())) {
                checked = true;
                break;
            }
        }
        return FindPlanEvaluationResult.findByPlanEvaluation(planEvaluations.size(), checked);
    }

    @Override
    public void deletePlanEvaluation(PlanEvaluationCommand command) {
        Plan plan = validatePlan(command.getPlan_id());
        EnteredView enteredView = validateEnteredView(plan.getParticipantInfo().getRoomId());

        if (!Objects.equals(enteredView.getCurrentWeek(), plan.getWeek())
                || plan.getThreeDaysPassed()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (planEvaluationRepository.existsEval(plan.getPlanId(), enteredView.getParticipantId())) {
            planEvaluationRepository.deletePlanEvaluation(plan.getPlanId(), enteredView.getParticipantId());
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

    private EnteredView validateEnteredView(Long room_id) {
        EnteredView enteredView = viewRepository.enteredByUserIdRoomId(getCurrentUserId(), room_id);

        if (enteredView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return enteredView;
    }
}
