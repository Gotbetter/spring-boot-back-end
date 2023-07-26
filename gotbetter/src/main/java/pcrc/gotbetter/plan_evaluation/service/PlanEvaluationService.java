package pcrc.gotbetter.plan_evaluation.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluationId;
import pcrc.gotbetter.plan_evaluation.data_access.repository.PlanEvaluationRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

@Service
public class PlanEvaluationService implements PlanEvaluationOperationUseCase, PlanEvaluationReadUseCase {
	private final PlanEvaluationRepository planEvaluationRepository;
	private final PlanRepository planRepository;
	private final DetailPlanRepository detailPlanRepository;
	private final ParticipantRepository participantRepository;

	@Autowired
	public PlanEvaluationService(PlanEvaluationRepository planEvaluationRepository, PlanRepository planRepository,
		DetailPlanRepository detailPlanRepository, ParticipantRepository participantRepository) {
		this.planEvaluationRepository = planEvaluationRepository;
		this.planRepository = planRepository;
		this.detailPlanRepository = detailPlanRepository;
		this.participantRepository = participantRepository;
	}

	@Override
	@Transactional
	public FindPlanEvaluationResult createPlanEvaluation(PlanEvaluationCommand command) {
		// 평가를 할 수 있는 상태인지 확인
		PlanDto planDto = validatePlanRoom(command.getPlanId());
		Participant evaluator = validateCanEvaluate(planDto);
		Plan planInfo = planDto.getPlan();
		Room roomInfo = planDto.getRoom();
		// 평가 리스트 조회
		List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(
			command.getPlanId());
		boolean checked = false;
		int planEvalSize = 0;

		// 과반수인지 확인
		if (Math.floor(roomInfo.getCurrentUserNum() - 1) / 2 < planEvaluations.size() + 1) {
			// 재작성 표시 업데이트
			planInfo.updateRejected(true);
			planInfo.updateById("SERVER");
			planRepository.save(planInfo);
			// 기존 평가들 삭제
			planEvaluationRepository.deleteByPlanEvaluationIdPlanId(planInfo.getPlanId());
			// 기존 세부계획들 삭제
			detailPlanRepository.deleteByPlanId(planInfo.getPlanId());
		} else {
			// 계획 평가 생성
			PlanEvaluation planEvaluation = PlanEvaluation.builder()
				.planEvaluationId(PlanEvaluationId.builder()
					.planId(planInfo.getPlanId())
					.participantId(evaluator.getParticipantId())
					.userId(evaluator.getUserId())
					.roomId(evaluator.getRoomId())
					.build())
				.build();
			planEvaluationRepository.save(planEvaluation);
			checked = true;
			planEvalSize = planEvaluations.size() + 1;
		}
		return FindPlanEvaluationResult.findByPlanEvaluation(planInfo, planEvalSize, checked);
	}

	@Override
	public FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query) {
		Plan plan = validatePlan(query.getPlanId());
		// 사용자가 방에 속해 있는지 확인
		Participant participant = validateUserInRoom(plan.getParticipantInfo().getRoomId());
		List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(query.getPlanId());
		boolean checked = false;

		for (PlanEvaluation p : planEvaluations) {
			if (Objects.equals(p.getPlanEvaluationId().getUserId(), participant.getUserId())) {
				checked = true;
				break;
			}
		}
		return FindPlanEvaluationResult.findByPlanEvaluation(plan, planEvaluations.size(), checked);
	}

	@Override
	public void deletePlanEvaluation(PlanEvaluationCommand command) {
		// // plan 정보 조회
		PlanDto planDto = validatePlanRoom(command.getPlanId());
		Plan planInfo = planDto.getPlan();
		Room roomInfo = planDto.getRoom();
		// 사용자가 방에 속해 있는지 확인
		Participant participant = validateUserInRoom(roomInfo.getRoomId());

		validateDate(planInfo, roomInfo.getCurrentWeek());
		if (planEvaluationRepository.existsEval(planInfo.getPlanId(), participant.getParticipantId())) {
			planEvaluationRepository.deleteById(PlanEvaluationId.builder()
				.planId(planInfo.getPlanId())
				.participantId(participant.getParticipantId())
				.userId(participant.getUserId())
				.roomId(participant.getRoomId())
				.build());
			return;
		}
		throw new GotBetterException(MessageType.NOT_FOUND);
	}

	/**
	 * validate
	 */
	private PlanDto validatePlanRoom(Long planId) {
		PlanDto planDto = planRepository.findPlanJoinRoom(planId);

		if (planDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return planDto;
	}

	private Participant validateCanEvaluate(PlanDto planDto) {
		Plan planInfo = planDto.getPlan();
		Room roomInfo = planDto.getRoom();
		// 사용자가 방에 속해 있는지 확인
		Participant participant = validateUserInRoom(roomInfo.getRoomId());

		// 사용자 본인의 계획인지 확인
		if (Objects.equals(planInfo.getParticipantInfo().getUserId(), getCurrentUserId())) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
		// 평가를 할 수 있는 날짜인지 확인
		validateDate(planInfo, roomInfo.getCurrentWeek());
		// 플랜이 재작성되어야하는 상태인지 확인
		if (planInfo.getRejected()) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
		// 세부 계획이 하나라도 작성되어있는지 확인
		if (!detailPlanRepository.existsByPlanId(planInfo.getPlanId())) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
		// 이미 평가한 계획인지
		if (planEvaluationRepository.existsEval(planInfo.getPlanId(), participant.getParticipantId())) {
			throw new GotBetterException(MessageType.CONFLICT);
		}
		return participant;
	}

	private Participant validateUserInRoom(Long roomId) {
		Participant participant = participantRepository.findByUserIdAndRoomId(getCurrentUserId(), roomId);
		if (participant == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return participant;
	}

	private Plan validatePlan(Long planId) {
		return planRepository.findByPlanId(planId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
	}

	private void validateDate(Plan plan, Integer currentWeek) {
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
