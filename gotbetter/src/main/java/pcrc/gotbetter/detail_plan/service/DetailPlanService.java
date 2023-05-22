package pcrc.gotbetter.detail_plan.service;

import org.springframework.stereotype.Service;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;
import pcrc.gotbetter.participant.data_access.repository.ViewRepository;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class DetailPlanService implements DetailPlanOperationUseCase, DetailPlanReadUseCase {
    private final DetailPlanRepository detailPlanRepository;
    private final PlanRepository planRepository;
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final RoomRepository roomRepository;
    private final ViewRepository viewRepository;

    public DetailPlanService(DetailPlanRepository detailPlanRepository, PlanRepository planRepository,
                             DetailPlanEvalRepository detailPlanEvalRepository, RoomRepository roomRepository,
                             ViewRepository viewRepository) {
        this.detailPlanRepository = detailPlanRepository;
        this.planRepository = planRepository;
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.roomRepository = roomRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public FindDetailPlanResult createDetailPlan(DetailPlanCreateCommand command) {
        Plan plan = validatePlan(command.getPlanId());
        Long currentUserId = getCurrentUserId();

        if (!Objects.equals(currentUserId, plan.getParticipantInfo().getUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        validateThreeDaysPassed(plan, roomRepository.findCurrentWeek(plan.getParticipantInfo().getRoomId()));
        if (plan.getRejected()) {
            planRepository.updateRejected(plan.getPlanId(), false);
        }
        DetailPlan detailPlan = DetailPlan.builder()
                .planId(plan.getPlanId())
                .participantInfo(ParticipantInfo.builder()
                        .participantId(plan.getParticipantInfo().getParticipantId())
                        .userId(plan.getParticipantInfo().getUserId())
                        .roomId(plan.getParticipantInfo().getRoomId())
                        .build())
                .content(command.getContent())
                .approveComment(null)
                .complete(false)
                .rejected(false)
                .build();
        detailPlanRepository.save(detailPlan);
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        return FindDetailPlanResult.findByDetailPlan(detailPlan, detailPlanEvals.size(), false);
    }

    @Override
    public List<FindDetailPlanResult> getDetailPlans(Long planId) {
        Plan plan = validatePlan(planId);
        Long currentUserId = getCurrentUserId();

        if (!viewRepository.enteredExistByUserIdRoomId(currentUserId, plan.getParticipantInfo().getRoomId())) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }

        List<DetailPlan> detailPlans = detailPlanRepository.findByPlanId(planId);
        List<FindDetailPlanResult> findDetailPlanResults = new ArrayList<>();

        for (DetailPlan d : detailPlans) {
            List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(d.getDetailPlanId());
            Integer dislike_cnt = detailPlanEvals.size();
            boolean checked = false;
            for (DetailPlanEval de : detailPlanEvals) {
                if (Objects.equals(de.getDetailPlanEvalId().getUserId(), currentUserId)) {
                    checked = true;
                    break;
                }
            }
            findDetailPlanResults.add(FindDetailPlanResult.findByDetailPlan(d, dislike_cnt, checked));
        }
        return findDetailPlanResults;
    }

    @Override
    public FindDetailPlanResult updateDetailPlan(DetailPlanUpdateCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getPlanId());

        validateThreeDaysPassed(validatePlan(detailPlan.getPlanId()),
                roomRepository.findCurrentWeek(detailPlan.getParticipantInfo().getRoomId()));

        detailPlanRepository.updateDetailContent(command.getDetailPlanId(), command.getContent());
        // detail plan eval 삭제하고, 완료 사항도 삭제?
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        return FindDetailPlanResult.builder()
                .detailPlanId(command.getDetailPlanId())
                .content(command.getContent())
                .complete(detailPlan.getComplete())
                .approveComment(detailPlan.getApproveComment() == null ? "" : detailPlan.getApproveComment())
                .rejected(detailPlan.getRejected())
                .planId(detailPlan.getPlanId())
                .detailPlanDislikeCount(detailPlanEvals.size())
                .detailPlanDislikeChecked(false)
                .build();
    }

    @Override
    public void deleteDetailPlan(DetailPlanDeleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getPlanId());

        validateThreeDaysPassed(validatePlan(detailPlan.getPlanId()),
                roomRepository.findCurrentWeek(detailPlan.getParticipantInfo().getRoomId()));

        detailPlanRepository.deleteDetailPlan(command.getDetailPlanId());
    }

    /**
     * validate
     */
    private Plan validatePlan(Long planId) {
        return planRepository.findByPlanId(planId)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
    }

    private DetailPlan validateDetailPlan(Long detailPlanId, Long planId) {
        DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detailPlanId);

        if (detailPlan == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        Long user_id = getCurrentUserId();
        if (!(Objects.equals(detailPlan.getPlanId(), planId)
                && Objects.equals(detailPlan.getParticipantInfo().getUserId(), user_id))) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        return detailPlan;
    }

    private void validateThreeDaysPassed(Plan plan, Integer currentWeek) {
        if (!Objects.equals(currentWeek, plan.getWeek())) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        } else {
            if (plan.getStartDate().isAfter(LocalDate.now())
                    || plan.getTargetDate().isBefore(LocalDate.now())) {
                throw new GotBetterException(MessageType.FORBIDDEN_DATE);
            }
        }
        if (plan.getThreeDaysPassed()) {
            throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        }
    }
}
