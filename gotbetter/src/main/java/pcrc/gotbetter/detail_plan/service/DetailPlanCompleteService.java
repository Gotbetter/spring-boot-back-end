package pcrc.gotbetter.detail_plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
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
public class DetailPlanCompleteService implements DetailPlanCompleteOperationUseCase {
    private final DetailPlanRepository detailPlanRepository;
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final RoomRepository roomRepository;
    private final PlanRepository planRepository;

    @Autowired
    public DetailPlanCompleteService(DetailPlanRepository detailPlanRepository,
                                     DetailPlanEvalRepository detailPlanEvalRepository,
                                     RoomRepository roomRepository, PlanRepository planRepository) {
        this.detailPlanRepository = detailPlanRepository;
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.roomRepository = roomRepository;
        this.planRepository = planRepository;
    }

    @Override
    public DetailPlanReadUseCase.FindDetailPlanResult completeDetailPlan(DetailPlanCompleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        Long user_id = getCurrentUserId();

        validateWeekPassed(detailPlan.getParticipantInfo().getRoomId(), detailPlan.getPlanId());
        if (!Objects.equals(user_id, detailPlan.getParticipantInfo().getUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (detailPlan.getRejected()) {
            detailPlanRepository.updateRejected(detailPlan.getDetailPlanId(), false);
        }
        if (detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        detailPlanRepository.updateDetailPlanCompleted(
                detailPlan.getDetailPlanId(), command.getApprove_comment());
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        return DetailPlanReadUseCase.FindDetailPlanResult
                .findByDetailPlanEval(detailPlan, command.getApprove_comment(), true
                        , detailPlanEvals.size(), false);
    }

    @Override
    @Transactional
    public DetailPlanReadUseCase.FindDetailPlanResult undoCompleteDetailPlan(DetailPlanCompleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        Long user_id = getCurrentUserId();

        validateWeekPassed(detailPlan.getParticipantInfo().getRoomId(), detailPlan.getPlanId());
        if (!Objects.equals(user_id, detailPlan.getParticipantInfo().getUserId())
        || detailPlan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (!detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        detailPlanRepository.updateDetailPlanUndo(detailPlan.getDetailPlanId(), false);
        detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        return DetailPlanReadUseCase.FindDetailPlanResult
                .findByDetailPlanEval(detailPlan, "", false
                        , detailPlanEvals.size(), false);
    }

    /**
     * validate
     */
    private DetailPlan validateDetailPlan(Long detail_plan_id, Long plan_id) {
        DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detail_plan_id);
        if (detailPlan == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (!Objects.equals(detailPlan.getPlanId(), plan_id)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return detailPlan;
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
            if (plan.getStartDate().isAfter(LocalDate.now())
                    || plan.getTargetDate().isBefore(LocalDate.now())) {
                throw new GotBetterException(MessageType.FORBIDDEN_DATE);
            }
        }
//        if (!plan.getThreeDaysPassed()) {
//            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
//        }
    }
}
