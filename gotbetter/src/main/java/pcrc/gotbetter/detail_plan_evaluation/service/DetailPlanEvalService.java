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
import java.util.HashMap;
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
    public DetailPlanEvalReadUseCase.FindDetailPlanEvalResult createDetailPlanEvaluation(DetailPlanEvaluationCommand command) throws InterruptedException {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId());
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
                .findByDetailPlanEvalIdDetailPlanId(command.getDetailPlanId());
        if (Math.floor(enteredView.getCurrentUserNum() - 1) / 2 < detailPlanEvals.size() + 1) {
            detailPlanRepository.updateDetailPlanUndo(detailPlan.getDetailPlanId(), true);
            detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
            // 여기 수정해야 함.
            HashMap<String, Object> data = detail_dislike_data(enteredView.getUserId(), command.getDetailPlanId());
            Thread.sleep(1000);
            DetailPlan d = validateDetailPlan(command.getDetailPlanId());
            System.out.println(d.getDetailPlanId() + ' ' + d.getContent() + ' ' + d.getComplete() + ' ' + d.getApproveComment() + ' ' + d.getRejected());
            return DetailPlanEvalReadUseCase.FindDetailPlanEvalResult.findByDetailPlanEval(
                    validateDetailPlan(command.getDetailPlanId()),
                    (Integer) data.get("detail_dislike_cnt"), (Boolean) data.get("detail_dislike_checked"));
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
            HashMap<String, Object> data = detail_dislike_data(enteredView.getUserId(), command.getDetailPlanId());
            return DetailPlanEvalReadUseCase.FindDetailPlanEvalResult.findByDetailPlanEval(detailPlan
                    , (Integer) data.get("detail_dislike_cnt"), (Boolean) data.get("detail_dislike_checked"));
        }
    }

    @Override
    public DetailPlanEvalReadUseCase.FindDetailPlanEvalResult deleteDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId());
        EnteredView enteredView = validateEnteredView(detailPlan.getParticipantInfo().getRoomId());

        validateWeekPassed(enteredView.getRoomId(),detailPlan.getPlanId());
        if (detailPlanEvalRepository.existsEval(detailPlan.getDetailPlanId(), enteredView.getParticipantId())) {
            detailPlanEvalRepository.deleteDetailPlanEval(detailPlan.getDetailPlanId(), enteredView.getParticipantId());
            HashMap<String, Object> data = detail_dislike_data(enteredView.getUserId(), command.getDetailPlanId());
            return DetailPlanEvalReadUseCase.FindDetailPlanEvalResult.findByDetailPlanEval(detailPlan
                    , (Integer) data.get("detail_dislike_cnt"), (Boolean) data.get("detail_dislike_checked"));
        }
        throw new GotBetterException(MessageType.NOT_FOUND);
    }

    /**
     * validate
     */
    private DetailPlan validateDetailPlan(Long detailPlanId) {
        DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detailPlanId);
        if (detailPlan == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return detailPlan;
    }

    private EnteredView validateEnteredView(Long roomId) {
        EnteredView enteredView = viewRepository.enteredByUserIdRoomId(getCurrentUserId(), roomId);

        if (enteredView == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return enteredView;
    }

    private void validateWeekPassed(Long roomId, Long planId) {
        Room room = roomRepository.findByRoomId(roomId).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
        Plan plan = planRepository.findByPlanId(planId).orElseThrow(() -> {
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

    private HashMap<String, Object> detail_dislike_data(Long userId, Long detailPlanId) {
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository
                .findByDetailPlanEvalIdDetailPlanId(detailPlanId);
        boolean checked = false;
        HashMap<String, Object> data = new HashMap<>();

        for (DetailPlanEval de : detailPlanEvals) {
            if (Objects.equals(de.getDetailPlanEvalId().getUserId(), userId)) {
                checked = true;
                break;
            }
        }
        data.put("detail_dislike_cnt", detailPlanEvals.size());
        data.put("detail_dislike_checked", checked);
        return data;
    }
}
