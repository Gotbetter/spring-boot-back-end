package pcrc.gotbetter.detail_plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class DetailPlanCompleteService implements DetailPlanCompleteOperationUseCase {
    private final DetailPlanRepository detailPlanRepository;
    private final DetailPlanEvalRepository detailPlanEvalRepository;

    @Autowired
    public DetailPlanCompleteService(DetailPlanRepository detailPlanRepository,
                                     DetailPlanEvalRepository detailPlanEvalRepository) {
        this.detailPlanRepository = detailPlanRepository;
        this.detailPlanEvalRepository = detailPlanEvalRepository;
    }

    @Override
    public DetailPlanReadUseCase.FindDetailPlanResult completeDetailPlan(DetailPlanCompleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        Long user_id = getCurrentUserId();

        if (!Objects.equals(user_id, detailPlan.getParticipantInfo().getUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (detailPlan.getRejected()) {
            detailPlanRepository.updateRejected(detailPlan.getDetailPlanId(), false);
        }
        detailPlanRepository.updateDetailPlanCompleted(
                detailPlan.getDetailPlanId(), command.getApprove_comment());
        return DetailPlanReadUseCase.FindDetailPlanResult.builder()
                .detail_plan_id(detailPlan.getDetailPlanId())
                .content(detailPlan.getContent())
                .complete(true)
                .approve_comment(command.getApprove_comment())
                .rejected(false)
                .plan_id(detailPlan.getPlanId())
                .build();
    }

    @Override
    @Transactional
    public DetailPlanReadUseCase.FindDetailPlanResult undoCompleteDetailPlan(DetailPlanCompleteCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id(), command.getPlan_id());
        Long user_id = getCurrentUserId();

        if (!Objects.equals(user_id, detailPlan.getParticipantInfo().getUserId())
        || detailPlan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        detailPlanRepository.updateDetailPlanUndo(detailPlan.getDetailPlanId(), false);
        detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        return DetailPlanReadUseCase.FindDetailPlanResult.builder()
                .detail_plan_id(detailPlan.getDetailPlanId())
                .content(detailPlan.getContent())
                .complete(false)
                .approve_comment("")
                .rejected(false)
                .plan_id(detailPlan.getPlanId())
                .build();
    }

    /**
     * validate
     */
    private DetailPlan validateDetailPlan(Long detail_plan_id, Long plan_id) {
        DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detail_plan_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
        if (!Objects.equals(detailPlan.getPlanId(), plan_id)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return detailPlan;
    }
}
