package pcrc.gotbetter.detail_plan_evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEvalId;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

import java.util.List;
import java.util.Objects;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class DetailPlanEvalService implements DetailPlanEvalOperationUseCase {
    private final DetailPlanEvalRepository detailPlanEvalRepository;
    private final DetailPlanRepository detailPlanRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    @Autowired
    public DetailPlanEvalService(DetailPlanEvalRepository detailPlanEvalRepository,
                                 DetailPlanRepository detailPlanRepository,
                                 RoomRepository roomRepository,
                                 ParticipantRepository participantRepository) {
        this.detailPlanEvalRepository = detailPlanEvalRepository;
        this.detailPlanRepository = detailPlanRepository;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    @Transactional
    public void createDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        DetailPlan detailPlan = validateDetailPlan(command.getDetail_plan_id());
        Long user_id = getCurrentUserId();
        Participant participant = validateMemberInRoom(user_id, detailPlan.getParticipantInfo().getRoomId());

        // 시간이 지났다면?
        if (detailPlan.getRejected()) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        if (detailPlanEvalRepository.existsEval(detailPlan.getDetailPlanId(), participant.getParticipantId())) {
            throw new GotBetterException(MessageType.CONFLICT);
        }

        Room room = validateRoom(detailPlan.getParticipantInfo().getRoomId());
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository
                .findByDetailPlanEvalIdDetailPlanId(command.getDetail_plan_id());
        if (Math.ceil(room.getCurrentUserNum()) / 2 <= detailPlanEvals.size() + 1) {
            detailPlanRepository.updateDetailPlanUndo(detailPlan.getDetailPlanId(), true);
            detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
        } else {
            DetailPlanEval detailPlanEval = DetailPlanEval.builder()
                    .detailPlanEvalId(DetailPlanEvalId.builder()
                            .detailPlanId(detailPlan.getDetailPlanId())
                            .planId(detailPlan.getPlanId())
                            .participantId(participant.getParticipantId())
                            .userId(participant.getUserId())
                            .roomId(participant.getRoomId())
                            .build())
                    .build();
            detailPlanEvalRepository.save(detailPlanEval);
        }
    }

    @Override
    @Transactional
    public void deleteDetailPlanEvaluation(DetailPlanEvaluationCommand command) {
        // 시간이 지났다면?
        List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository
                .findByDetailPlanEvalIdDetailPlanId(command.getDetail_plan_id());
        if (detailPlanEvals.size() == 0) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
        Long user_id = getCurrentUserId();
        for (DetailPlanEval d : detailPlanEvals) {
            if (Objects.equals(d.getDetailPlanEvalId().getUserId(), user_id)) {
                detailPlanEvalRepository.deleteByDetailPlanEvalId(d.getDetailPlanEvalId());
                return;
            }
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

    private Room validateRoom(Long room_id) {
        return roomRepository.findByRoomId(room_id).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
    }

    private Participant validateMemberInRoom(Long user_id, Long room_id) {
        Participant participant = participantRepository.findByUserIdAndRoomId(user_id, room_id);
        if (participant == null) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
        return participant;
    }
}
