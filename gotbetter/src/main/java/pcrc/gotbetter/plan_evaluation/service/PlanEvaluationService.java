package pcrc.gotbetter.plan_evaluation.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.plan_evaluation.data_access.dto.PlanEvaluationDto;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluation;
import pcrc.gotbetter.plan_evaluation.data_access.entity.PlanEvaluationId;
import pcrc.gotbetter.plan_evaluation.data_access.repository.PlanEvaluationRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class PlanEvaluationService implements PlanEvaluationOperationUseCase, PlanEvaluationReadUseCase {
	@Value("${local.default.profile.image}")
	String PROFILE_LOCAL_DEFAULT_IMG;
	@Value("${server.default.profile.image}")
	String PROFILE_SERVER_DEFAULT_IMG;
	private final PlanEvaluationRepository planEvaluationRepository;
	private final PlanRepository planRepository;
	private final DetailPlanRepository detailPlanRepository;
	private final ParticipantRepository participantRepository;
	private final UserRepository userRepository;

	@Autowired
	public PlanEvaluationService(PlanEvaluationRepository planEvaluationRepository, PlanRepository planRepository,
		DetailPlanRepository detailPlanRepository, ParticipantRepository participantRepository,
		UserRepository userRepository) {
		this.planEvaluationRepository = planEvaluationRepository;
		this.planRepository = planRepository;
		this.detailPlanRepository = detailPlanRepository;
		this.participantRepository = participantRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public FindPlanEvaluationResult createPlanEvaluation(PlanEvaluationCommand command) {
		// 평가를 할 수 있는 상태인지 확인
		PlanDto planDto = validatePlanRoom(command.getPlanId());
		Participant evaluator = validateCanEvaluate(planDto, false, getCurrentUserId());
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
	@Transactional
	public FindPlanEvaluationResult createPlanEvaluationAdmin(PlanEvaluationAdminCommand command) {
		validateIsAdmin();

		// 평가를 할 수 있는 상태인지 확인
		PlanDto planDto = validatePlanRoom(command.getPlanId());
		Participant evaluator = validateCanEvaluate(planDto, true, command.getUserId());
		Plan planInfo = planDto.getPlan();
		Room roomInfo = planDto.getRoom();
		// 평가 리스트 조회
		List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(
			command.getPlanId());

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
		}
		return FindPlanEvaluationResult.findByPlanEvaluation(planInfo, null, null);
	}

	@Override
	public FindPlanEvaluationResult getPlanDislike(PlanEvaluationFindQuery query) {
		Plan plan = validatePlan(query.getPlanId());
		// 사용자가 방에 속해 있는지 확인
		Participant participant = validateUserInRoom(plan.getParticipantInfo().getRoomId(), getCurrentUserId());
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
	public List<FindPlanDislikeListResult> getPlanDislikeList(PlanEvaluationFindQuery query) throws IOException {
		validateIsAdmin();

		Plan plan = validatePlan(query.getPlanId());
		List<PlanEvaluationDto> planEvaluationDtos = planEvaluationRepository.findDislikeUsers(plan.getPlanId());
		List<FindPlanDislikeListResult> results = new ArrayList<>();
		for (PlanEvaluationDto planEvaluationDto : planEvaluationDtos) {
			String bytes = getByteProfile(planEvaluationDto.getUser().getProfile());
			results.add(FindPlanDislikeListResult.findByPlanDislikeList(planEvaluationDto, bytes));
		}
		return results;
	}

	@Override
	public List<FindPlanDislikeListResult> getPlanNotDislikeList(PlanEvaluationFindQuery query) throws IOException {
		validateIsAdmin();

		Plan plan = validatePlan(query.getPlanId());
		List<ParticipantDto> membersInRoom = participantRepository.findMembersByPlanId(query.getPlanId());

		if (membersInRoom.size() == 0) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		List<PlanEvaluation> planEvaluations = planEvaluationRepository.findByPlanEvaluationIdPlanId(query.getPlanId());
		List<FindPlanDislikeListResult> results = new ArrayList<>();

		for (ParticipantDto participantDto : membersInRoom) {
			Long participantId = participantDto.getParticipant().getParticipantId();
			if (Objects.equals(plan.getParticipantInfo().getUserId(), participantDto.getUser().getUserId())) {
				continue;
			}
			if (planEvaluations.size() != 0) {
				boolean found = planEvaluations.stream()
					.anyMatch(planEvaluation -> planEvaluation.getPlanEvaluationId()
						.getParticipantId()
						.equals(participantId));
				if (found) {
					continue;
				}
			}
			String bytes = getByteProfile(participantDto.getUser().getProfile());
			results.add(FindPlanDislikeListResult.findByPlanNotDislikeList(participantDto, bytes, query.getPlanId()));
		}
		return results;
	}

	@Override
	public void deletePlanEvaluation(PlanEvaluationCommand command) {
		// // plan 정보 조회
		PlanDto planDto = validatePlanRoom(command.getPlanId());
		Plan planInfo = planDto.getPlan();
		Room roomInfo = planDto.getRoom();
		// 사용자가 방에 속해 있는지 확인
		Participant participant = validateUserInRoom(roomInfo.getRoomId(), getCurrentUserId());

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

	@Override
	public void deletePlanEvaluationAdmin(PlanEvaluationDeleteAdminCommand command) {
		validateIsAdmin();

		// plan 정보 조회
		Plan plan = validatePlan(command.getPlanId());
		Participant participant = participantRepository.findByParticipantId(command.getParticipantId());

		if (participant == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		if (planEvaluationRepository.existsEval(plan.getPlanId(), participant.getParticipantId())) {
			planEvaluationRepository.deleteById(PlanEvaluationId.builder()
				.planId(plan.getPlanId())
				.participantId(participant.getParticipantId())
				.roomId(participant.getRoomId())
				.userId(participant.getUserId())
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

	private Participant validateCanEvaluate(PlanDto planDto, Boolean admin, Long userId) {
		Plan planInfo = planDto.getPlan();
		Room roomInfo = planDto.getRoom();
		// 사용자가 방에 속해 있는지 확인
		Participant participant = validateUserInRoom(roomInfo.getRoomId(), userId);

		// 사용자 본인의 계획인지 확인
		if (Objects.equals(planInfo.getParticipantInfo().getUserId(), userId)) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
		if (!admin) {
			// 평가를 할 수 있는 날짜인지 확인
			validateDate(planInfo, roomInfo.getCurrentWeek());
		}
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

	private Participant validateUserInRoom(Long roomId, Long userId) {
		Participant participant = participantRepository.findByUserIdAndRoomId(userId, roomId);
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

	private void validateIsAdmin() {
		User requestUser = userRepository.findByUserId(getCurrentUserId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (requestUser.getRoleType() == RoleType.ADMIN || requestUser.getRoleType() == RoleType.MAIN_ADMIN) {
			return;
		}
		throw new GotBetterException(MessageType.FORBIDDEN_ADMIN);
	}

	private String getByteProfile(String profile) throws IOException {
		String bytes;

		try {
			bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(profile)));
		} catch (Exception e) {
			String os = System.getProperty("os.name").toLowerCase();
			String dir =
				os.contains("win") ? PROFILE_LOCAL_DEFAULT_IMG : PROFILE_SERVER_DEFAULT_IMG;
			bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
		}
		return bytes;
	}
}
