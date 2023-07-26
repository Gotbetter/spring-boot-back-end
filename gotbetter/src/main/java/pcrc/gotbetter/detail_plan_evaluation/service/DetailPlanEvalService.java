package pcrc.gotbetter.detail_plan_evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEvalId;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.detail_plan_record.data_access.repository.DetailPlanRecordRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
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
public class DetailPlanEvalService implements DetailPlanEvalOperationUseCase {
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final DetailPlanRepository detailPlanRepository;
    private final PlanRepository planRepository;
    private final ParticipantRepository participantRepository;
    private final DetailPlanRecordRepository detailPlanRecordRepository;

    @Autowired
    public DetailPlanEvalService(DetailPlanEvalRepository detailPlanEvalRepository,
                                 DetailPlanRepository detailPlanRepository,
                                 PlanRepository planRepository,
                                 ParticipantRepository participantRepository,
        DetailPlanRecordRepository detailPlanRecordRepository) {
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.detailPlanRepository = detailPlanRepository;
        this.planRepository = planRepository;
        this.participantRepository = participantRepository;
        this.detailPlanRecordRepository = detailPlanRecordRepository;
    }

    @Override
    @Transactional
    public DetailPlanEvalReadUseCase.FindDetailPlanEvalResult createDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId());
        PlanDto planDto = validatePlanRoom(detailPlan.getPlanId());
        Participant evaluator = validateCanEvaluate(planDto, detailPlan);
        Room room = planDto.getRoom();
        Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(command.getDetailPlanId());
        boolean checked = false;

        if (Math.floor(room.getCurrentUserNum() - 1) / 2 < detailPlanEvalSize + 1) {
            detailPlan.updateDetailPlanUndo(true);
            detailPlan.updateById("SERVER");
            detailPlanRepository.save(detailPlan);
            detailPlanRecordRepository.deleteByDetailPlanIdDetailPlanId(detailPlan.getDetailPlanId());
            detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
            detailPlanEvalSize = 0;
        } else {
            DetailPlanEval detailPlanEval = DetailPlanEval.builder()
                .detailPlanEvalId(DetailPlanEvalId.builder()
                    .detailPlanId(detailPlan.getDetailPlanId())
                    .planId(detailPlan.getPlanId())
                    .participantId(evaluator.getParticipantId())
                    .userId(evaluator.getUserId())
                    .roomId(evaluator.getRoomId())
                    .build())
                .build();
            detailPlanEvalRepository.save(detailPlanEval);
            checked = true;
            detailPlanEvalSize = detailPlanEvalSize + 1;
        }
        return DetailPlanEvalReadUseCase.FindDetailPlanEvalResult.findByDetailPlanEval(detailPlan
            , detailPlanEvalSize, checked);
    }

    @Override
    @Transactional
    public DetailPlanEvalReadUseCase.FindDetailPlanEvalResult deleteDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId());
        PlanDto planDto = validatePlanRoom(detailPlan.getPlanId());
        Participant participant = validateUserInRoom(detailPlan.getParticipantInfo().getRoomId());

        validateWeekPassed(planDto);
        if (detailPlanEvalRepository.existsEval(detailPlan.getDetailPlanId(), participant.getParticipantId())) {
            detailPlanEvalRepository.deleteByDetailPlanEvalId(DetailPlanEvalId.builder()
                    .detailPlanId(detailPlan.getDetailPlanId())
                    .planId(detailPlan.getPlanId())
                    .participantId(participant.getParticipantId())
                    .userId(participant.getUserId())
                    .roomId(participant.getRoomId())
                .build());

            Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(command.getDetailPlanId());

            return DetailPlanEvalReadUseCase.FindDetailPlanEvalResult.findByDetailPlanEval(
                detailPlan, detailPlanEvalSize, false);
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

    private PlanDto validatePlanRoom(Long planId) {
        PlanDto planDto = planRepository.findPlanJoinRoom(planId);

        if (planDto == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return planDto;
    }

    private Participant validateCanEvaluate (PlanDto planDto, DetailPlan detailPlan) {
        Room roomInfo = planDto.getRoom();
        Participant evaluator = validateUserInRoom(roomInfo.getRoomId());

        // 자기 자신
        if (Objects.equals(detailPlan.getParticipantInfo().getUserId(), getCurrentUserId())) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        validateWeekPassed(planDto);
        if (detailPlan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (!detailPlan.getComplete()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (detailPlanEvalRepository.existsEval(detailPlan.getDetailPlanId(), evaluator.getParticipantId())) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
        return evaluator;
    }

    private Participant validateUserInRoom(Long roomId) {
        Participant participant = participantRepository.findByUserIdAndRoomId(getCurrentUserId(), roomId);

        if (participant == null) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        return participant;
    }

    private void validateWeekPassed(PlanDto planDto) {
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
        // if (!plan.getThreeDaysPassed()) {
        //     throw new GotBetterException(MessageType.FORBIDDEN_DATE);
        // }
    }
}
