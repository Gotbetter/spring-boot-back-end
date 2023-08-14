package pcrc.gotbetter.detail_plan.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;
import pcrc.gotbetter.detail_plan_record.data_access.repository.DetailPlanRecordRepository;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class DetailPlanCompleteService implements DetailPlanCompleteOperationUseCase {
	private final DetailPlanRepository detailPlanRepository;
	private final DetailPlanEvalRepository detailPlanEvalRepository;
	private final PlanRepository planRepository;
	private final UserRepository userRepository;
	private final DetailPlanRecordRepository detailPlanRecordRepository;
	private final ParticipantRepository participantRepository;

	@Autowired
	public DetailPlanCompleteService(
		DetailPlanRepository detailPlanRepository,
		DetailPlanEvalRepository detailPlanEvalRepository,
		PlanRepository planRepository,
		UserRepository userRepository, DetailPlanRecordRepository detailPlanRecordRepository,
		ParticipantRepository participantRepository) {
		this.detailPlanRepository = detailPlanRepository;
		this.detailPlanEvalRepository = detailPlanEvalRepository;
		this.planRepository = planRepository;
		this.userRepository = userRepository;
		this.detailPlanRecordRepository = detailPlanRecordRepository;
		this.participantRepository = participantRepository;
	}

	@Override
	@Transactional
	public DetailPlanReadUseCase.FindDetailPlanResult completeDetailPlan(DetailPlanCompleteCommand command) {
		if (command.getAdmin()) {
			validateIsAdmin();
		}
		DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getPlanId());

		if (!command.getAdmin()) {
			Long currentUserId = getCurrentUserId();

			validateWeekPassed(detailPlan.getPlanId());
			if (!Objects.equals(currentUserId, detailPlan.getParticipantInfo().getUserId())) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
		}
		if (detailPlan.getComplete()) {
			throw new GotBetterException(MessageType.CONFLICT);
		}
		List<DetailPlanRecord> detailPlanRecords = detailPlanRecordRepository.findByDetailPlanIdDetailPlanId(
			detailPlan.getDetailPlanId());

		if (detailPlanRecords.size() == 0) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
		detailPlan.updateDetailPlanCompleted();
		detailPlanRepository.save(detailPlan);
		if (command.getAdmin()) {
			updateScore(command.getPlanId());
		}

		Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(
			detailPlan.getDetailPlanId());

		return DetailPlanReadUseCase.FindDetailPlanResult
			.findByDetailPlan(detailPlan, detailPlanEvalSize, false);
	}

	@Override
	@Transactional
	public DetailPlanReadUseCase.FindDetailPlanResult undoCompleteDetailPlan(DetailPlanCompleteCommand command) {
		DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getPlanId());

		if (!command.getAdmin()) {
			Long currentUserId = getCurrentUserId();

			validateWeekPassed(detailPlan.getPlanId());
			if (!Objects.equals(currentUserId, detailPlan.getParticipantInfo().getUserId())
				|| detailPlan.getRejected()) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
		}
		if (!detailPlan.getComplete()) {
			throw new GotBetterException(MessageType.CONFLICT);
		}
		detailPlan.updateDetailPlanUndo(false);
		detailPlanRepository.save(detailPlan);
		/** TODO  세부 계획 평가 지워야 하나? */
		detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
		if (command.getAdmin()) {
			updateScore(command.getPlanId());
		}
		Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(
			detailPlan.getDetailPlanId());

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

	private void validateIsAdmin() {
		User requestUser = userRepository.findByUserId(getCurrentUserId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (requestUser.getRoleType() == RoleType.ADMIN || requestUser.getRoleType() == RoleType.MAIN_ADMIN) {
			return;
		}
		throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
	}

	private void updateScore(Long planId) {
		Plan plan = planRepository.findByPlanId(planId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
		LocalDate now = LocalDate.now();

		if (!now.isAfter(plan.getTargetDate())) {
			return;
		}
		Participant participant = participantRepository.findByParticipantId(
			plan.getParticipantInfo().getParticipantId());

		if (participant == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		HashMap<String, Long> map = detailPlanRepository.countCompleteTrue(plan.getPlanId());
		Long size = map.get("size");
		Long completeCount = map.get("completeCount");

		float divide = size != 0 ? (float)completeCount / (float)size : 0;
		float percent = Math.round(divide * 1000) / 10.0F;
		Float prevScore = plan.getScore();

		plan.updateScore(percent);
		planRepository.save(plan);

		participant.updatePercentSum(-prevScore + percent);
		participantRepository.save(participant);
	}
}
