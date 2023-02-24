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
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class DetailPlanService implements DetailPlanOperationUseCase, DetailPlanReadUseCase {
    private final DetailPlanRepository detailPlanRepository;
    private final PlanRepository planRepository;
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final ViewRepository viewRepository;

    public DetailPlanService(DetailPlanRepository detailPlanRepository, PlanRepository planRepository,
                             DetailPlanEvalRepository detailPlanEvalRepository, ViewRepository viewRepository) {
        this.detailPlanRepository = detailPlanRepository;
        this.planRepository = planRepository;
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.viewRepository = viewRepository;
    }

    @Override
    public FindDetailPlanResult createDetailPlan(DetailPlanCreateCommand command) {
        Plan plan = validatePlan(command.getPlan_id());
        Long user_id = getCurrentUserId();

        if (!Objects.equals(user_id, plan.getParticipantInfo().getUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (plan.getThreeDaysPassed()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
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
                .approve_comment(null)
                .complete(false)
                .rejected(false)
                .build();
        detailPlanRepository.save(detailPlan);
        return FindDetailPlanResult.findByDetailPlan(detailPlan, null, null);
    }

    @Override
    public List<FindDetailPlanResult> getDetailPlans(Long plan_id) {
        Plan plan = validatePlan(plan_id);
        Long user_id = getCurrentUserId();

        if (!viewRepository.enteredExistByUserIdRoomId(user_id, plan.getParticipantInfo().getRoomId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }

        List<DetailPlan> detailPlans = detailPlanRepository.findByPlanId(plan_id);
        List<FindDetailPlanResult> findDetailPlanResults = new ArrayList<>();

        for (DetailPlan d : detailPlans) {
            List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(d.getDetailPlanId());
            Integer dislike_cnt = detailPlanEvals.size();
            boolean checked = false;
            for (DetailPlanEval de : detailPlanEvals) {
                if (Objects.equals(de.getDetailPlanEvalId().getUserId(), user_id)) {
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
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        validateThreeDaysPassed(command.getPlan_id());
        detailPlanRepository.updateDetailContent(command.getDetail_plan_id(), command.getContent());
        return FindDetailPlanResult.builder()
                .detail_plan_id(command.getDetail_plan_id())
                .content(command.getContent())
                .complete(detailPlan.getComplete())
                .approve_comment(detailPlan.getApprove_comment() == null ? "" : detailPlan.getApprove_comment())
                .rejected(detailPlan.getRejected())
                .plan_id(detailPlan.getPlanId())
                .build();
    }

    @Override
    public void deleteDetailPlan(DetailPlanDeleteCommand command) {
        validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        validateThreeDaysPassed(command.getPlan_id());
        detailPlanRepository.deleteDetailPlan(command.getDetail_plan_id());
    }

    /**
     * validate
     */
    private Plan validatePlan(Long plan_id) {
        return planRepository.findByPlanId(plan_id)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
    }

    private DetailPlan validateDetailPlan(Long detail_plan_id, Long plan_id) {
        DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detail_plan_id)
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        Long user_id = getCurrentUserId();
        if (!(Objects.equals(detailPlan.getPlanId(), plan_id)
                && Objects.equals(detailPlan.getParticipantInfo().getUserId(), user_id))) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        return detailPlan;
    }

    private void validateThreeDaysPassed(Long plan_id) {
        if (planRepository.existsByThreeDaysPassed(plan_id)) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }
}
