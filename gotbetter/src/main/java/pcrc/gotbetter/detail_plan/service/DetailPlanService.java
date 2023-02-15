package pcrc.gotbetter.detail_plan.service;

import org.springframework.stereotype.Service;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;

    public DetailPlanService(DetailPlanRepository detailPlanRepository, PlanRepository planRepository,
                             ParticipantRepository participantRepository) {
        this.detailPlanRepository = detailPlanRepository;
        this.planRepository = planRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public FindDetailPlanResult createDetailPlan(DetailPlanCreateCommand command) {
        Plan plan = validatePlan(command.getPlan_id());
        Long user_id = getCurrentUserId();

        if (!Objects.equals(user_id, plan.getUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (plan.getRejected()) {
            planRepository.updateRejected(plan.getPlanId(), false);
        }
        DetailPlan detailPlan = DetailPlan.builder()
                .planId(plan.getPlanId())
                .participantId(plan.getParticipantId())
                .userId(plan.getUserId())
                .roomId(plan.getRoomId())
                .content(command.getContent())
                .complete(false)
                .build();
        detailPlanRepository.save(detailPlan);
        return FindDetailPlanResult.findByDetailPlan(detailPlan);
    }

    @Override
    public List<FindDetailPlanResult> getDetailPlans(Long plan_id) {
        Plan plan = validatePlan(plan_id);
        Long user_id = getCurrentUserId();

        if (!participantRepository.existsMemberInRoom(user_id, plan.getRoomId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }

        List<DetailPlan> detailPlans = detailPlanRepository.findByPlanId(plan_id);
        List<FindDetailPlanResult> findDetailPlanResults = new ArrayList<>();

        for (DetailPlan d : detailPlans) {
            findDetailPlanResults.add(FindDetailPlanResult.findByDetailPlan(d));
        }
        return findDetailPlanResults;
    }

    @Override
    public FindDetailPlanResult updateDetailPlan(DetailPlanUpdateCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        if (detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        detailPlanRepository.updateDetailContent(command.getDetail_plan_id(), command.getContent());
        return FindDetailPlanResult.builder()
                .detail_plan_id(command.getDetail_plan_id())
                .content(command.getContent())
                .complete(detailPlan.getComplete())
                .plan_id(detailPlan.getPlanId())
                .build();
    }

    @Override
    public void deleteDetailPlan(DetailPlanDeleteCommand command) {
        validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
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
                && Objects.equals(detailPlan.getUserId(), user_id))) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        return detailPlan;
    }
}
