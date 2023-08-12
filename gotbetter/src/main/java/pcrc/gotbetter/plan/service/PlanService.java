package pcrc.gotbetter.plan.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.entity.ParticipantInfo;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class PlanService implements PlanOperationUseCase, PlanReadUseCase {
	private final PlanRepository planRepository;
	private final ParticipantRepository participantRepository;
	private final UserRepository userRepository;

	@Autowired
	public PlanService(
		PlanRepository planRepository,
		ParticipantRepository participantRepository,
		UserRepository userRepository) {
		this.planRepository = planRepository;
		this.participantRepository = participantRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<FindPlanResult> createPlans(PlanCreateCommand command) { // (방장) 자신 및 멤버들의 플랜 틀 생성
		// 사용자가 방의 멤버인지 확인
		ParticipantDto participantRoom = validateParticipantRoom(command.getParticipantId());
		Participant participantInfo = participantRoom.getParticipant();
		Room roomInfo = participantRoom.getRoom();

		if (command.getAdmin()) {
			validateIsAdmin();
		} else {
			// 방장인지 확인
			validateSenderIsLeader(roomInfo.getRoomId());
		}
		// 이미 존재하는지 확인
		validateDuplicateCreatePlans(command.getParticipantId());
		// 현재 진행 중인 방인지 확인 - 종료된 방이면 생성되지 않음.
		validateDate(roomInfo);

		// 플랜 틀 생성
		List<Plan> plans = new ArrayList<>();
		for (int i = 1; i <= roomInfo.getWeek(); i++) {
			Plan plan = Plan.builder()
				.participantInfo(ParticipantInfo.builder()
					.participantId(participantInfo.getParticipantId())
					.userId(participantInfo.getUserId())
					.roomId(participantInfo.getRoomId())
					.build())
				.startDate(roomInfo.getStartDate().plusDays((i - 1) * 7L))
				.targetDate(roomInfo.getStartDate().plusDays(i * 7L - 1))
				.score(0.0F)
				.week(i)
				.threeDaysPassed(false)
				.rejected(false)
				.build();
			plans.add(plan);
		}

		List<Plan> planList = planRepository.saveAll(plans);
		List<FindPlanResult> results = new ArrayList<>();

		for (Plan plan : planList) {
			results.add(FindPlanResult.findByPlan(plan));
		}
		return results;
	}

	@Override
	public FindPlanResult getWeekPlan(PlanFindQuery query) {
		// 사용자가 방의 멤버인지 확인
		Participant participant = validateParticipant(query.getParticipantId());
		// 요청한 사용자가 방의 멤버인지 확인
		validateSenderInRoom(participant.getRoomId());

		// 요청한 주차의 플랜 조회
		Plan plan = planRepository.findWeekPlanOfUser(query.getParticipantId(), query.getWeek());
		if (plan == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return FindPlanResult.findByPlan(plan);
	}

	/**
	 * validate
	 */
	private ParticipantDto validateParticipantRoom(Long participantId) {
		ParticipantDto participantDto = participantRepository.findParticipantRoomByParticipantId(participantId);

		if (participantDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return participantDto;
	}

	private void validateSenderIsLeader(Long roomId) {
		long currentUserId = getCurrentUserId();
		if (!participantRepository.isMatchedLeader(currentUserId, roomId)) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
	}

	private void validateDuplicateCreatePlans(Long participantId) {
		if (planRepository.existsByParticipantId(participantId)) {
			throw new GotBetterException(MessageType.CONFLICT);
		}
	}

	private Participant validateParticipant(Long participantId) {
		Participant participant = participantRepository.findByParticipantId(participantId);

		if (participant == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		return participant;
	}

	private void validateSenderInRoom(Long roomId) {
		long currentUserId = getCurrentUserId();

		if (!participantRepository.existsByUserIdAndRoomId(currentUserId, roomId)) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
	}

	private void validateDate(Room room) {
		LocalDate lastDate = room.getStartDate().plusDays(7L * room.getWeek() - 1);

		if (lastDate.isBefore(LocalDate.now())) {
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
