package pcrc.gotbetter.detail_plan_evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEvalId;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.participant.data_access.view.EnteredView;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class DetailPlanEvalService implements DetailPlanEvalOperationUseCase {
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final DetailPlanRepository detailPlanRepository;
    private final RoomRepository roomRepository;
    private final PlanRepository planRepository;
    private final ViewRepository viewRepository;

    @Autowired
    public DetailPlanEvalService(DetailPlanEvalRepository detailPlanEvalRepository,
                                 DetailPlanRepository detailPlanRepository,
                                 RoomRepository roomRepository,
                                 PlanRepository planRepository, ViewRepository viewRepository) {
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.detailPlanRepository = detailPlanRepository;
        this.roomRepository = roomRepository;
        this.planRepository = planRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    @Transactional
    public void createDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id());
        EnteredView enteredView = validateEnteredView(detailPlan.getParticipantInfo().getRoomId());

        validateWeekPassed(enteredView.getRoomId(),detailPlan.getPlanId());
        if (detailPlan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (!detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (detailPlanEvalRepository.existsEval(detailPlan.getDetailPlanId(), enteredView.getParticipantId())) {
            throw new GotBetterException(MessageType.CONFLICT);
        }

        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository
                .findByDetailPlanEvalIdDetailPlanId(command.getDetail_plan_id());
        if (Math.ceil(enteredView.getCurrentUserNum()) / 2 <= detailPlanEvals.size() + 1) {
            detailPlanRepository.updateDetailPlanUndo(detailPlan.getDetailPlanId(), true);
            detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        } else {
            DetailPlanEval detailPlanEval = DetailPlanEval.builder()
                    .detailPlanEvalId(DetailPlanEvalId.builder()
                            .detailPlanId(detailPlan.getDetailPlanId())
                            .planId(detailPlan.getPlanId())
                            .participantId(enteredView.getParticipantId())
                            .userId(enteredView.getUserId())
                            .roomId(enteredView.getRoomId())
                            .build())
                    .build();
            detailPlanEvalRepository.save(detailPlanEval);
        }
    }

    @Override
    public void deleteDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id());
        EnteredView enteredView = validateEnteredView(detailPlan.getParticipantInfo().getRoomId());

        validateWeekPassed(enteredView.getRoomId(),detailPlan.getPlanId());
        if (detailPlanEvalRepository.existsEval(detailPlan.getDetailPlanId(), enteredView.getParticipantId())) {
            detailPlanEvalRepository.deleteDetailPlanEval(detailPlan.getDetailPlanId(), enteredView.getParticipantId());
            return;
        }
        throw new GotBetterException(MessageType.NOT_FOUND);
    }

    /**
     * validate
     */
    private DetailPlan validateDetailPlan(Long detail_plan_id) {
        return detailPlanRepository.findByDetailPlanId(detail_plan_id).orElseThrow(() -> {
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

    private void validateWeekPassed(Long room_id, Long plan_id) {
        Room room = roomRepository.findByRoomId(room_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
        Plan plan = planRepository.findByPlanId(plan_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
        if (!Objects.equals(room.getCurrentWeek(), plan.getWeek())) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        } else {
            if (plan.getTargetDate().isBefore(LocalDate.now())) {
                throw new GotBetterException(MessageType.FORBIDDEN_DATE);
            }
        }
    }
}
