package pcrc.gotbetter.detail_plan_evaluation.service;

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

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.dto.DetailPlanEvalDto;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEvalId;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.detail_plan_record.data_access.repository.DetailPlanRecordRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
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
public class DetailPlanEvalService implements DetailPlanEvalOperationUseCase, DetailPlanEvalReadUseCase {
	@Value("${local.default.profile.image}")
	String PROFILE_LOCAL_DEFAULT_IMG;
	@Value("${server.default.profile.image}")
	String PROFILE_SERVER_DEFAULT_IMG;
	private final DetailPlanEvalRepository detailPlanEvalRepository;
	private final DetailPlanRepository detailPlanRepository;
	private final PlanRepository planRepository;
	private final ParticipantRepository participantRepository;
	private final DetailPlanRecordRepository detailPlanRecordRepository;
	private final UserRepository userRepository;

	@Autowired
	public DetailPlanEvalService(
		DetailPlanEvalRepository detailPlanEvalRepository,
		DetailPlanRepository detailPlanRepository,
		PlanRepository planRepository,
		ParticipantRepository participantRepository,
		DetailPlanRecordRepository detailPlanRecordRepository,
		UserRepository userRepository) {
		this.detailPlanEvalRepository = detailPlanEvalRepository;
		this.detailPlanRepository = detailPlanRepository;
		this.planRepository = planRepository;
		this.participantRepository = participantRepository;
		this.detailPlanRecordRepository = detailPlanRecordRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public DetailPlanEvalReadUseCase.FindDetailPlanEvalResult createDetailPlanEvaluation(
		DetailPlanEvaluationCommand command
	) {
		DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId());
		PlanDto planDto = validatePlanRoom(detailPlan.getPlanId());
		Participant evaluator = validateCanEvaluate(planDto, detailPlan);
		Room room = planDto.getRoom();
		Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(
			command.getDetailPlanId());
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
	public DetailPlanEvalReadUseCase.FindDetailPlanEvalResult deleteDetailPlanEvaluation(
		DetailPlanEvaluationCommand command
	) {
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

			Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(
				command.getDetailPlanId());

			return DetailPlanEvalReadUseCase.FindDetailPlanEvalResult.findByDetailPlanEval(
				detailPlan, detailPlanEvalSize, false);
		}
		throw new GotBetterException(MessageType.NOT_FOUND);
	}

	@Override
	public List<FindDetailDislikeListResult> getDetailDislikeList(DetailDislikeFindQuery query) throws IOException {
		validateIsAdmin();

		DetailPlan detailPlan = validateDetailPlan(query.getDetailPlanId());
		List<DetailPlanEvalDto> detailPlanEvalDtos = detailPlanEvalRepository.findDislikeUsers(
			detailPlan.getDetailPlanId());
		List<FindDetailDislikeListResult> results = new ArrayList<>();

		for (DetailPlanEvalDto detailPlanEvalDto : detailPlanEvalDtos) {
			String bytes = getByteProfile(detailPlanEvalDto.getUser().getProfile());
			results.add(FindDetailDislikeListResult.findByDetailDislikeList(detailPlanEvalDto, bytes));
		}
		return results;
	}

	@Override
	public List<FindDetailDislikeListResult> getDetailNotDislikeList(DetailDislikeFindQuery query) throws IOException {
		validateIsAdmin();

		DetailPlan detailPlan = validateDetailPlan(query.getDetailPlanId());
		List<ParticipantDto> membersInRoom = participantRepository.findMembersByDetailId(detailPlan.getDetailPlanId());

		if (membersInRoom.size() == 0) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(
			detailPlan.getDetailPlanId());
		List<FindDetailDislikeListResult> results = new ArrayList<>();

		for (ParticipantDto participantDto : membersInRoom) {
			Long participantId = participantDto.getParticipant().getParticipantId();
			if (Objects.equals(detailPlan.getParticipantInfo().getUserId(), participantDto.getUser().getUserId())) {
				continue;
			}
			if (detailPlanEvals.size() != 0) {
				boolean found = detailPlanEvals.stream()
					.anyMatch(detailPlanEval -> detailPlanEval.getDetailPlanEvalId()
						.getParticipantId()
						.equals(participantId));
				if (found) {
					continue;
				}
			}
			String bytes = getByteProfile(participantDto.getUser().getProfile());
			results.add(
				FindDetailDislikeListResult.findByDetailNotDislikeList(participantDto, bytes, detailPlan.getPlanId(),
					detailPlan.getDetailPlanId()));
		}
		return results;
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

	private Participant validateCanEvaluate(PlanDto planDto, DetailPlan detailPlan) {
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
