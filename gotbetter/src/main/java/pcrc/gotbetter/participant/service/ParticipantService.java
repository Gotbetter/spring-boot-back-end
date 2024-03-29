package pcrc.gotbetter.participant.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.JoinRequestRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.room.service.RoomReadUseCase;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class ParticipantService implements ParticipantOperationUseCase, ParticipantReadUseCase {
	@Value("${local.default.profile.image}")
	String PROFILE_LOCAL_DEFAULT_IMG;
	@Value("${server.default.profile.image}")
	String PROFILE_SERVER_DEFAULT_IMG;
	@Value("${local.default.my.profile.image}")
	String PROFILE_LOCAL_DEFAULT_MY_IMG;
	@Value("${server.default.my.profile.image}")
	String PROFILE_SERVER_DEFAULT_MY_IMG;
	private final ParticipantRepository participantRepository;
	private final JoinRequestRepository joinRequestRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;

	@Autowired
	public ParticipantService(
		ParticipantRepository participantRepository,
		JoinRequestRepository joinRequestRepository,
		RoomRepository roomRepository,
		UserRepository userRepository) {
		this.participantRepository = participantRepository;
		this.joinRequestRepository = joinRequestRepository;
		this.roomRepository = roomRepository;
		this.userRepository = userRepository;
	}

	@Override
	public RoomReadUseCase.FindRoomResult requestJoinRoom(String roomCode) { // 방 입장 (멤버x)
		Room room = validateRoomWithRoomCode(roomCode); // 방 코드에 해당하는 방이 있는지 확인
		Long currentUserId = validateAbleToJoinRoom(room, getCurrentUserId()); // 해당 방에 사용자가 입장할 수 있는지 확인

		// 종료된 방인지 확인
		validateDate(room);

		// 승인 요청
		JoinRequest joinRequest = JoinRequest.builder()
			.joinRequestId(JoinRequestId.builder()
				.userId(currentUserId)
				.roomId(room.getRoomId())
				.build())
			.accepted(false)
			.build();
		joinRequestRepository.save(joinRequest);

		return RoomReadUseCase.FindRoomResult.builder()
			.roomId(room.getRoomId())
			.entryFee(room.getEntryFee())
			.account(room.getAccount())
			.build();
	}

	@Override
	public void adminRequestJoinRoom(AdminJoinRequestCommand command) {
		validateIsAdmin();
		Room room = validateRoomWithRoomCode(command.getRoomCode()); // 방 코드에 해당하는 방이 있는지 확인
		Long currentUserId = validateAbleToJoinRoom(room, command.getUserId()); // 해당 방에 사용자가 입장할 수 있는지 확인

		// 승인 요청
		JoinRequest joinRequest = JoinRequest.builder()
			.joinRequestId(JoinRequestId.builder()
				.userId(currentUserId)
				.roomId(room.getRoomId())
				.build())
			.accepted(false)
			.build();
		joinRequestRepository.save(joinRequest);
	}

	@Override
	public List<FindParticipantResult> getMemberListInARoom(ParticipantsFindQuery query) throws IOException {
		List<FindParticipantResult> result = new ArrayList<>();
		Room room = roomRepository.findByRoomId(query.getRoomId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		if (query.getAccepted()) { // (방장 포함 일반 멤버) 방에 속한 멤버들 조회
			if (query.getAdmin()) {
				validateIsAdmin();
			} else {
				validateUserInRoom(query.getRoomId(), false); // 방에 속한 멤버인지 검증
			}

			List<ParticipantDto> participantDtoList = getRoomMemberList(query);
			int currentWeek = room.getCurrentWeek();
			LocalDate now = LocalDate.now();
			LocalDate afterOneDayOfLastDay = room.getStartDate().plusDays(7L * room.getCurrentWeek());

			if (now.isBefore(afterOneDayOfLastDay)) {
				currentWeek -= 1;
			}
			for (ParticipantDto p : participantDtoList) {
				float divide = p.getParticipant().getPercentSum() / (float)(currentWeek * 100);
				Float percent = Math.round(divide * 1000) / 10.0F;

				result.add(FindParticipantResult.findByParticipant(
					p, getProfileBytes(p.getUser()), percent, query.getAdmin()));
			}
		} else { // (방장만) 승인 대기 중인 사용자 조회
			if (query.getAdmin()) {
				validateIsAdmin();
			} else {
				validateUserInRoom(query.getRoomId(), true); // 방장인지 검증
			}
			List<JoinRequestDto> joinRequestList = joinRequestRepository.findJoinRequestJoinList(null,
				query.getRoomId(), false);
			for (JoinRequestDto joinRequest : joinRequestList) {
				result.add(FindParticipantResult.findByParticipant(joinRequest,
					-1L, false, getProfileBytes(joinRequest.getUser()), query.getAdmin()));
			}
		}
		return result;
	}

	@Override
	@Transactional
	public FindParticipantResult approveJoinRoom(UserRoomAcceptedCommand command) throws IOException { // (방장) 방 입장 승인
		if (command.getAdmin()) {
			validateIsAdmin();
		} else {
			// 방장인지 검증
			validateUserInRoom(command.getRoomId(), true);
		}

		// 승인하려는 사용자 정보
		JoinRequestDto joinRequestDto = joinRequestRepository.findJoinRequestJoin(
			command.getUserId(), command.getRoomId(), false);

		if (joinRequestDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		JoinRequest joinRequestInfo = joinRequestDto.getJoinRequest();
		Room roomInfo = joinRequestDto.getRoom();

		if (!command.getAdmin()) {
			// 현재 진행 중인 방인지 확인 - 종료된 방이면 승인되지 않음.
			validateDate(roomInfo);
		}
		// 방의 인원이 만원인지 확인
		if (Objects.equals(roomInfo.getMaxUserNum(), roomInfo.getCurrentUserNum())) {
			throw new GotBetterException(MessageType.CONFLICT_MAX);
		}
		// 방 승인
		Participant participant = Participant.builder()
			.userId(joinRequestInfo.getJoinRequestId().getUserId())
			.roomId(joinRequestInfo.getJoinRequestId().getRoomId())
			.authority(false)
			.refund(0)
			.build();
		participantRepository.save(participant);
		// 사용자 방 요청을 수락했으므로 accepted를 true로 변경
		joinRequestInfo.updateAcceptedToJoin();
		joinRequestRepository.save(joinRequestInfo);
		// 방의 전체 입장비와 인원수 변경
		roomInfo.updateTotalEntryFeeAndCurrentUserNum(roomInfo.getEntryFee());
		roomRepository.save(roomInfo);
		// 프로필 수정 추가해야함.
		return FindParticipantResult.findByParticipant(joinRequestDto,
			participant.getParticipantId(), null, getProfileBytes(joinRequestDto.getUser()), false);
	}

	@Override
	public void rejectJoinRoom(UserRoomAcceptedCommand command) { // (방장) 방 입장 요청 거절
		if (command.getAdmin()) {
			validateIsAdmin();
		} else {
			// 방장인지 검증
			validateUserInRoom(command.getRoomId(), true);
		}
		// 존재하는 요청자인지 확인 - join request
		JoinRequest joinRequest = joinRequestRepository.findJoinRequest(command.getUserId(), command.getRoomId());

		if (joinRequest == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		// 거절 - join request
		joinRequestRepository.deleteById(joinRequest.getJoinRequestId());
	}

	@Override
	public Integer getMyRefund(Long participantId) { // 마지막 주차 끝난 후 조회 가능
		ParticipantDto participantDto = participantRepository.findParticipantRoomByParticipantId(participantId);

		// 방의 멤버인지 확인
		if (participantDto == null
			|| !Objects.equals(participantDto.getParticipant().getUserId(), getCurrentUserId())) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		Participant participant = participantDto.getParticipant();
		Room room = participantDto.getRoom();

		// 마지막 주차가 종료됐는지 확인
		if (!Objects.equals(room.getWeek(), room.getCurrentWeek())) {
			throw new GotBetterException(MessageType.FORBIDDEN_DATE);
		} else {
			LocalDate now = LocalDate.now();
			LocalDate lastDate = room.getStartDate().plusDays(7L * room.getCurrentWeek() - 1);
			if (!now.isAfter(lastDate)) {
				throw new GotBetterException(MessageType.FORBIDDEN_DATE);
			}
		}
		return participant.getRefund();
	}

	@Override
	@Transactional
	public void deleteParticipant(Long participantId) {
		validateIsAdmin();

		Participant participant = participantRepository.findByParticipantId(participantId);

		// participantId 있는지 확인
		if (participant == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		// 방장인 경우 - 일단 거절
		if (participant.getAuthority()) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}

		// 인원 수 -1
		Room room = roomRepository.findByRoomId(participant.getRoomId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		// validateDate(room);
		/** TODO  total_entry_fee는 어떻게 하지? */
		room.decreaseCurrentUserNum();
		roomRepository.save(room);

		// join-request delete
		JoinRequest joinRequest = joinRequestRepository.findByJoinRequestId(JoinRequestId.builder()
			.userId(participant.getUserId())
			.roomId(participant.getRoomId())
			.build()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		joinRequestRepository.delete(joinRequest);
	}

	/**
	 * validate section
	 */
	private Room validateRoomWithRoomCode(String roomCode) {
		return roomRepository.findByRoomCode(roomCode).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
	}

	private Long validateAbleToJoinRoom(Room room, Long userId) {
		Optional<JoinRequest> participate = joinRequestRepository.findByJoinRequestId(
			JoinRequestId.builder()
				.userId(userId)
				.roomId(room.getRoomId()).build()
		);

		if (participate.isPresent()) {
			if (participate.get().getAccepted()) { // 이미 방에 참여했는지 확인
				throw new GotBetterException(MessageType.CONFLICT_JOIN);
			} else { // 아직 방에 참여 승인 받지 않았지만 승인 요청을 보내놓은 상태
				throw new GotBetterException(MessageType.CONFLICT);
			}
		}
		// 방에 참여하지 않았고, 승인 요청도 보내놓은 상태가 아닌 경우
		// 방의 인원이 만원인지 확인
		if (room.getCurrentUserNum() >= room.getMaxUserNum()) {
			throw new GotBetterException(MessageType.CONFLICT_MAX);
		}
		return userId;
	}

	private void validateUserInRoom(Long roomId, Boolean needLeader) {
		long currentUserId = getCurrentUserId();
		Participant participant = participantRepository.findByUserIdAndRoomId(currentUserId, roomId);

		if (participant == null) { // 사용자가 방에 속해 있지 않은 경우 (오류)
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		if (needLeader && !participant.getAuthority()) { // 방장의 권한이 필요하지만 해당 방의 방장이 아닌 경우 (오류)
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
	}

	private void validateDate(Room room) {
		LocalDate lastDate = room.getStartDate().plusDays(7L * room.getWeek() - 1);

		if (lastDate.isBefore(LocalDate.now())) {
			throw new GotBetterException(MessageType.FORBIDDEN_DATE);
		}
	}

	private String getProfileBytes(User user) throws IOException {
		String bytes;
		String dir;
		String os = System.getProperty("os.name").toLowerCase();

		if ((Objects.equals(user.getProfile(), PROFILE_SERVER_DEFAULT_IMG) || Objects.equals(user.getProfile(),
			PROFILE_LOCAL_DEFAULT_IMG))) {
			if (Objects.equals(user.getUserId(), getCurrentUserId())) {
				dir = os.contains("win") ? PROFILE_LOCAL_DEFAULT_MY_IMG : PROFILE_SERVER_DEFAULT_MY_IMG;
			} else {
				dir = os.contains("win") ? PROFILE_LOCAL_DEFAULT_IMG : PROFILE_SERVER_DEFAULT_IMG;
			}
		} else {
			dir = user.getProfile();
		}
		try {
			bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
		} catch (Exception e) {
			if (Objects.equals(user.getUserId(), getCurrentUserId())) {
				dir = os.contains("win") ? PROFILE_LOCAL_DEFAULT_MY_IMG : PROFILE_SERVER_DEFAULT_MY_IMG;
			} else {
				dir = os.contains("win") ? PROFILE_LOCAL_DEFAULT_IMG : PROFILE_SERVER_DEFAULT_IMG;
			}
			bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
		}
		return bytes;
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

	private List<ParticipantDto> getRoomMemberList(ParticipantsFindQuery query) {
		List<ParticipantDto> participantDtoList = participantRepository.findUserInfoList(query.getRoomId());
		int targetIndex;

		if (!query.getAdmin()) {
			for (targetIndex = 0; targetIndex < participantDtoList.size(); targetIndex++) {
				long target = participantDtoList.get(targetIndex).getUser().getUserId();

				if (target == getCurrentUserId()) {
					break;
				}
			}
			participantDtoList.add(0, participantDtoList.get(targetIndex));
			participantDtoList.remove(targetIndex + 1);
		}
		return participantDtoList;
	}
}
