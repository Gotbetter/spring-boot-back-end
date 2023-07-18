package pcrc.gotbetter.detail_plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.time.LocalDate;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class DetailPlanCompleteService implements DetailPlanCompleteOperationUseCase {
    private final DetailPlanRepository detailPlanRepository;
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final PlanRepository planRepository;

    @Autowired
    public DetailPlanCompleteService(DetailPlanRepository detailPlanRepository,
                                     DetailPlanEvalRepository detailPlanEvalRepository,
                                     PlanRepository planRepository) {
        this.detailPlanRepository = detailPlanRepository;
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.planRepository = planRepository;
    }

    @Override
    public DetailPlanReadUseCase.FindDetailPlanResult completeDetailPlan(DetailPlanCompleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getPlanId());
        Long currentUserId = getCurrentUserId();

        validateWeekPassed(detailPlan.getPlanId());
        if (!Objects.equals(currentUserId, detailPlan.getParticipantInfo().getUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        if (detailPlan.getRejected()) {
            detailPlan.updateRejected(false);
        }
        detailPlan.updateDetailPlanCompleted(command.getApproveComment());
        detailPlanRepository.save(detailPlan);

        Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());

        return DetailPlanReadUseCase.FindDetailPlanResult
            .findByDetailPlan(detailPlan, detailPlanEvalSize, false);
    }

    @Override
    @Transactional
    public DetailPlanReadUseCase.FindDetailPlanResult undoCompleteDetailPlan(DetailPlanCompleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getPlanId());
        Long currentUserId = getCurrentUserId();

        validateWeekPassed(detailPlan.getPlanId());
        if (!Objects.equals(currentUserId, detailPlan.getParticipantInfo().getUserId())
        || detailPlan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (!detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        detailPlan.updateDetailPlanUndo(false);
        detailPlanRepository.save(detailPlan);
        detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());

        Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());

        return DetailPlanReadUseCase.FindDetailPlanResult
                .findByDetailPlan(detailPlan, detailPlanEvalSize, false);
    }

    /**
     * validate
     */
    private DetailPlan validateDetailPlan(Long detailPlanId, Long planId) {
        DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detailPlanId);

        if (detailPlan == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        if (!Objects.equals(detailPlan.getPlanId(), planId)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return detailPlan;
    }

    private void validateWeekPassed(Long planId) {
        PlanDto planDto = planRepository.findPlanJoinRoom(planId);

        if (planDto == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        Plan plan = planDto.getPlan();
        Room room = planDto.getRoom();

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
