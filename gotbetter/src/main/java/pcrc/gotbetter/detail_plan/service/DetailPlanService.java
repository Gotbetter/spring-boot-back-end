package pcrc.gotbetter.detail_plan.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pcrc.gotbetter.detail_plan.data_access.dto.DetailPlanDto;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;
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
public class DetailPlanService implements DetailPlanOperationUseCase, DetailPlanReadUseCase {
	private final DetailPlanRepository detailPlanRepository;
	private final PlanRepository planRepository;
	private final DetailPlanEvalRepository detailPlanEvalRepository;
	private final ParticipantRepository participantRepository;
	private final UserRepository userRepository;

	public DetailPlanService(
		DetailPlanRepository detailPlanRepository,
		PlanRepository planRepository,
		DetailPlanEvalRepository detailPlanEvalRepository,
		ParticipantRepository participantRepository,
		UserRepository userRepository) {
		this.detailPlanRepository = detailPlanRepository;
		this.planRepository = planRepository;
		this.detailPlanEvalRepository = detailPlanEvalRepository;
		this.participantRepository = participantRepository;
		this.userRepository = userRepository;
	}

	@Override
	public FindDetailPlanResult createDetailPlan(DetailPlanCreateCommand command) {
		PlanDto planDto = validatePlanRoom(command.getPlanId());
		Plan plan = planDto.getPlan();
		Room room = planDto.getRoom();

		if (command.getAdmin()) {
			validateIsAdmin();
		} else {
			Long currentUserId = getCurrentUserId();

			if (!Objects.equals(currentUserId, plan.getParticipantInfo().getUserId())) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
			validateThreeDaysPassed(plan.getPlanId(), room.getCurrentWeek());
		}
		if (plan.getRejected()) {
			plan.updateRejected(false);
			planRepository.save(plan);
		}
		DetailPlan detailPlan = DetailPlan.builder()
			.planId(plan.getPlanId())
			.participantInfo(ParticipantInfo.builder()
				.participantId(plan.getParticipantInfo().getParticipantId())
				.userId(plan.getParticipantInfo().getUserId())
				.roomId(plan.getParticipantInfo().getRoomId())
				.build())
			.content(command.getContent())
			.complete(false)
			.rejected(false)
			.build();
		detailPlanRepository.save(detailPlan);

		Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(
			detailPlan.getDetailPlanId());

		return FindDetailPlanResult.findByDetailPlan(detailPlan, detailPlanEvalSize, false);
	}

	@Override
	public List<FindDetailPlanResult> getDetailPlans(DetailPlanFindQuery query) {
		Plan plan = validatePlan(query.getPlanId());

		if (!query.getAdmin()) {
			Long currentUserId = getCurrentUserId();

			if (!participantRepository.existsByUserIdAndRoomId(currentUserId, plan.getParticipantInfo().getRoomId())) {
				throw new GotBetterException(MessageType.NOT_FOUND);
			}
		} else {
			validateIsAdmin();
		}

		/** Todo detailPlanEval - detailPlan 좀 더 생각해보기 */
		List<DetailPlan> detailPlans = detailPlanRepository.findByPlanId(query.getPlanId());
		List<FindDetailPlanResult> findDetailPlanResults = new ArrayList<>();

		for (DetailPlan d : detailPlans) {
			List<DetailPlanEval> detailPlanEvals = detailPlanEvalRepository.findByDetailPlanEvalIdDetailPlanId(
				d.getDetailPlanId());
			Integer dislike_cnt = detailPlanEvals.size();
			boolean checked = false;
			for (DetailPlanEval de : detailPlanEvals) {
				if (Objects.equals(de.getDetailPlanEvalId().getUserId(), plan.getParticipantInfo().getUserId())) {
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
		DetailPlanDto detailPlanDto = validateDetailPlanRoom(command.getDetailPlanId(), command.getPlanId(),
			command.getAdmin());
		DetailPlan detailPlan = detailPlanDto.getDetailPlan();
		Room room = detailPlanDto.getRoom();

		if (command.getAdmin()) {
			validateIsAdmin();
		} else {
			validateThreeDaysPassed(detailPlan.getPlanId(), room.getCurrentWeek());
		}
		detailPlan.updateContent(command.getContent());
		detailPlanRepository.save(detailPlan);
		/** Todo detail plan eval 삭제하고, 완료 사항도 삭제? => ?? */
		Integer detailPlanEvalSize = detailPlanEvalRepository.countByDetailPlanEvalIdDetailPlanId(
			detailPlan.getDetailPlanId());

		return FindDetailPlanResult.findByDetailPlan(detailPlan, detailPlanEvalSize, false);
	}

	@Override
	@Transactional
	public void deleteDetailPlan(DetailPlanDeleteCommand command) {
		DetailPlanDto detailPlanDto = validateDetailPlanRoom(command.getDetailPlanId(), command.getPlanId(),
			command.getAdmin());
		DetailPlan detailPlan = detailPlanDto.getDetailPlan();
		Room room = detailPlanDto.getRoom();

		if (command.getAdmin()) {
			validateIsAdmin();
		} else {
			validateThreeDaysPassed(detailPlan.getPlanId(), room.getCurrentWeek());
		}
		detailPlanRepository.deleteByDetailPlanId(detailPlan.getDetailPlanId());
	}

	/**
	 * validate
	 */
	private Plan validatePlan(Long planId) {
		return planRepository.findByPlanId(planId)
			.orElseThrow(() -> {
				throw new GotBetterException(MessageType.NOT_FOUND);
			});
	}

	private DetailPlanDto validateDetailPlanRoom(Long detailPlanId, Long planId, Boolean admin) {
		DetailPlanDto detailPlanDto = detailPlanRepository.findByDetailJoinRoom(detailPlanId);

		if (detailPlanDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		DetailPlan detailPlan = detailPlanDto.getDetailPlan();

		if (admin) {
			validateIsAdmin();
		} else {
			Long user_id = getCurrentUserId();

			if (!(Objects.equals(detailPlan.getPlanId(), planId)
				&& Objects.equals(detailPlan.getParticipantInfo().getUserId(), user_id))) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
		}
		return detailPlanDto;
	}

	private PlanDto validatePlanRoom(Long planId) {
		PlanDto planDto = planRepository.findPlanJoinRoom(planId);

		if (planDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return planDto;
	}

	private void validateThreeDaysPassed(Long planId, Integer currentWeek) {
		Plan plan = validatePlan(planId);

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
}
