package pcrc.gotbetter.room.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;
import pcrc.gotbetter.common.data_access.repository.CommonCodeRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;
import pcrc.gotbetter.participant.data_access.entity.JoinRequestId;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.JoinRequestRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class RoomService implements RoomOperationUseCase, RoomReadUseCase {
	@Value("${local.default.profile.image}")
	String PROFILE_LOCAL_DEFAULT_IMG;
	@Value("${server.default.profile.image}")
	String PROFILE_SERVER_DEFAULT_IMG;
	@Value("${local.default.my.profile.image}")
	String PROFILE_LOCAL_DEFAULT_MY_IMG;
	@Value("${server.default.my.profile.image}")
	String PROFILE_SERVER_DEFAULT_MY_IMG;
	private final RoomRepository roomRepository;
	private final JoinRequestRepository joinRequestRepository;
	private final ParticipantRepository participantRepository;
	private final CommonCodeRepository commonCodeRepository;
	private final UserRepository userRepository;

	@Autowired
	public RoomService(
		RoomRepository roomRepository,
		JoinRequestRepository joinRequestRepository,
		ParticipantRepository participantRepository,
		CommonCodeRepository commonCodeRepository,
		UserRepository userRepository) {
		this.roomRepository = roomRepository;
		this.joinRequestRepository = joinRequestRepository;
		this.participantRepository = participantRepository;
		this.commonCodeRepository = commonCodeRepository;
		this.userRepository = userRepository;
	}

	@Override
	public FindRoomResult createRoom(RoomCreateCommand command) {
		Long currentUserId = getCurrentUserId();
		// 방 코드 생성
		String roomCode = getRandomCode();
		// 방 계획 시작 날짜
		LocalDate startDate = LocalDate.parse(command.getStartDate(), DateTimeFormatter.ISO_DATE);

		// 방 계획 시작 날짜가 과거 날짜인지 확인
		if (startDate.isBefore(LocalDate.now())) {
			throw new GotBetterException(MessageType.FORBIDDEN_DATE);
		}

		// category 정보
		CommonCode roomCategoryInfo = findRoomCategoryInfo(command.getRoomCategoryCode());

		// rule 정보 - 고정된 규칙
		CommonCode ruleInfo = findRuleInfo(command.getRuleCode());
		// rule 정보 - 커스텀

		// 방 데이터 insert
		Room room = Room.builder()
			.title(command.getTitle())
			.maxUserNum(command.getMaxUserNum())
			.currentUserNum(1)
			.startDate(startDate)
			.week(command.getWeek())
			.currentWeek(command.getCurrentWeek())
			.entryFee(command.getEntryFee())
			.roomCode(roomCode)
			.account(command.getAccount())
			.roomCategory(roomCategoryInfo.getCommonCodeId().getCode())
			.description(command.getDescription())
			.totalEntryFee(command.getEntryFee())
			.rule(ruleInfo.getCommonCodeId().getCode())
			.build();
		roomRepository.save(room);

		// join request 데이터 insert
		JoinRequest joinRequest = JoinRequest.builder()
			.joinRequestId(JoinRequestId.builder()
				.userId(currentUserId)
				.roomId(room.getRoomId())
				.build())
			.accepted(true)
			.build();
		joinRequestRepository.save(joinRequest);

		// participant 데이터 insert
		Participant participant = Participant.builder()
			.userId(joinRequest.getJoinRequestId().getUserId())
			.roomId(joinRequest.getJoinRequestId().getRoomId())
			.authority(true)
			.refund(0)
			.build();
		participantRepository.save(participant);

		return FindRoomResult.findByRoom(room, participant.getParticipantId(),
			roomCategoryInfo.getCodeDescription(),
			ruleInfo.getCodeDescription());
	}

	@Override
	public FindRoomResult createRoomAdmin(RoomCreateCommand command) {
		validateIsAdmin();
		// 방 코드 생성
		String roomCode = getRandomCode();
		// 방 계획 시작 날짜
		LocalDate startDate = LocalDate.parse(command.getStartDate(), DateTimeFormatter.ISO_DATE);

		// category 정보
		CommonCode roomCategoryInfo = findRoomCategoryInfo(command.getRoomCategoryCode());

		// rule 정보 - 고정된 규칙
		CommonCode ruleInfo = findRuleInfo(command.getRuleCode());
		// rule 정보 - 커스텀

		Integer currentWeek = 0;
		LocalDate startDateOfTargetWeek = startDate;
		LocalDate now = LocalDate.now();
		for (int i = 1; i <= command.getWeek(); i++) {
			currentWeek++;
			LocalDate endDateOfTargetWeek = startDateOfTargetWeek.plusDays(6L);
			if (startDateOfTargetWeek.isAfter(now) || !(startDateOfTargetWeek.isAfter(now) || now.isAfter(
				endDateOfTargetWeek))) {
				break;
			}
			startDateOfTargetWeek = startDateOfTargetWeek.plusDays(7L);
		}

		// 방 데이터 insert
		Room room = Room.builder()
			.title(command.getTitle())
			.maxUserNum(command.getMaxUserNum())
			.currentUserNum(1)
			.startDate(startDate)
			.week(command.getWeek())
			.currentWeek(currentWeek)
			.entryFee(command.getEntryFee())
			.roomCode(roomCode)
			.account(command.getAccount())
			.roomCategory(roomCategoryInfo.getCommonCodeId().getCode())
			.rule(ruleInfo.getCommonCodeId().getCode())
			.description(command.getDescription())
			.totalEntryFee(command.getEntryFee())
			.build();
		roomRepository.save(room);

		// join request 데이터 insert
		JoinRequest joinRequest = JoinRequest.builder()
			.joinRequestId(JoinRequestId.builder()
				.userId(command.getUserId())
				.roomId(room.getRoomId())
				.build())
			.accepted(true)
			.build();
		joinRequestRepository.save(joinRequest);

		// participant 데이터 insert
		Participant participant = Participant.builder()
			.userId(joinRequest.getJoinRequestId().getUserId())
			.roomId(joinRequest.getJoinRequestId().getRoomId())
			.authority(true)
			.refund(0)
			.build();
		participantRepository.save(participant);

		return FindRoomResult.findByRoom(room, participant.getParticipantId(),
			roomCategoryInfo.getCodeDescription(),
			ruleInfo.getCodeDescription());
	}

	@Override
	public FindRoomResult updateDescriptionRoom(RoomUpdateDescriptionCommand command) {
		// 사용자가 방에 속해있는지 확인
		ParticipantDto participantDto = participantRepository.findParticipantByUserIdAndRoomId(getCurrentUserId(),
			command.getRoom_id());

		if (participantDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		// 방장이 맞는지 확인 - participant
		Participant participant = participantDto.getParticipant();

		if (!participant.getAuthority()) {
			throw new GotBetterException(MessageType.FORBIDDEN);
		}
		// 방 소개 수정 - room
		Room room = participantDto.getRoom();

		room.updateDescription(command.getDescription());
		roomRepository.save(room);

		CommonCode roomCategoryInfo = findRoomCategoryInfo(room.getRoomCategory());
		CommonCode ruleInfo = findRuleInfo(room.getRule());

		return FindRoomResult.findByRoom(room, null,
			roomCategoryInfo.getCodeDescription(), ruleInfo.getCodeDescription());
	}

	@Override
	public List<FindRoomResult> getUserRoomList(Boolean admin) {
		if (admin) {
			validateIsAdmin();
		}

		// 유저가 속한 방 리스트 - JoinRequest 대신 Participant에서 사용 가능 - room 정보
		List<ParticipantDto> participantDtoList;
		List<FindRoomResult> results = new ArrayList<>();

		if (!admin) {
			participantDtoList = participantRepository.findRoomsByUserId(getCurrentUserId());
		} else {
			participantDtoList = participantRepository.findRoomsWithLeader();
		}

		// common code 모든 데이터 (ROOM_CATEGORY + RULE)
		List<CommonCode> commonCodes = commonCodeRepository.findListByGroupCode("");
		HashMap<String, CommonCode> commonCodeHashMap = new HashMap<>();

		for (CommonCode commonCode : commonCodes) {
			commonCodeHashMap.put(commonCode.getCommonCodeId().getGroupCode()
				+ "/" + commonCode.getCommonCodeId().getCode(), commonCode);
		}
		for (ParticipantDto participantDto : participantDtoList) {
			CommonCode roomCategoryInfo = commonCodeHashMap.get(
				"ROOM_CATEGORY/" + participantDto.getRoom().getRoomCategory());
			CommonCode ruleInfo = commonCodeHashMap.get("RULE/" + participantDto.getRoom().getRule());
			if (!admin) {
				results.add(FindRoomResult.findByRoom(participantDto, roomCategoryInfo.getCodeDescription(),
					ruleInfo.getCodeDescription()));
			} else {
				String endDate = participantDto.getRoom()
					.getStartDate()
					.plusDays(7L * participantDto.getRoom().getWeek() - 1)
					.toString();
				results.add(FindRoomResult.findByRoom(participantDto, roomCategoryInfo, ruleInfo, endDate));
			}
		}
		return results;
	}

	@Override
	public FindRoomResult getOneRoomInfo(RoomFindQuery query) {
		if (query.getAdmin()) {
			validateIsAdmin();
		}

		ParticipantDto participantDto;

		if (!query.getAdmin()) {
			// 유저가 속한 방 정보
			participantDto = participantRepository.findParticipantByUserIdAndRoomId(getCurrentUserId(),
				query.getRoomId());
		} else {
			participantDto = participantRepository.findRoomWithLeaderByRoomId(query.getRoomId());
		}
		if (participantDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		// 카테고리 정보
		CommonCode roomCategoryInfo = findRoomCategoryInfo(participantDto.getRoom().getRoomCategory());
		// rule 정보 - 고정된 규칙
		CommonCode ruleInfo = findRuleInfo(participantDto.getRoom().getRule());

		if (!query.getAdmin()) {
			return FindRoomResult.findByRoom(participantDto, roomCategoryInfo.getCodeDescription(),
				ruleInfo.getCodeDescription());
		} else {
			String endDate = participantDto.getRoom()
				.getStartDate()
				.plusDays(7L * participantDto.getRoom().getWeek() - 1)
				.toString();
			return FindRoomResult.findByRoom(participantDto, roomCategoryInfo, ruleInfo, endDate);
		}
	}

	@Override
	public List<FindRankResult> getRank(Long roomId) throws IOException {
		Long currentUserId = getCurrentUserId();

		if (!participantRepository.existsByUserIdAndRoomId(currentUserId, roomId)) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		List<ParticipantDto> participantDtoList = participantRepository.findParticipantRoomByRoomId(roomId);
		List<FindRankResult> findRankResultList = new ArrayList<>();
		LocalDate now = LocalDate.now();

		if (participantDtoList.size() == 0 ||
			now.isBefore(participantDtoList.get(0).getRoom().getStartDate())) {
			return findRankResultList;
		}

		Room room = participantDtoList.get(0).getRoom();
		Map<Float, List<HashMap<String, String>>> percentMap = new HashMap<>();

		for (ParticipantDto participantDto : participantDtoList) {
			User user = participantDto.getUser();
			Participant participant = participantDto.getParticipant();
			Float key = participant.getPercentSum();
			List<HashMap<String, String>> userInfoList =
				(percentMap.containsKey(key)) ? percentMap.get(key) : new ArrayList<>();
			HashMap<String, String> userInfo = new HashMap<>();

			userInfo.put("profile", getProfileBytes(user));
			userInfo.put("username", user.getUsername());
			userInfoList.add(userInfo);
			percentMap.put(key, userInfoList);
		}

		List<Float> keySet = new ArrayList<>(percentMap.keySet());

		keySet.sort(Comparator.reverseOrder());

		int rank = 1;
		int rankId = 0;
		int leftRefund =
			keySet.size() == 1 ? 0 : room.getEntryFee() * percentMap.get(keySet.get(keySet.size() - 1)).size();

		for (Float key : keySet) {
			List<HashMap<String, String>> userInfoList = percentMap.get(key);

			for (HashMap<String, String> userInfo : userInfoList) {
				int refund = room.getEntryFee();

				if (rank == 1) {
					if (room.getCurrentWeek() == 1) {
						refund = 0;
					} else {
						refund += (Math.floor(leftRefund) / userInfoList.size());
					}
				} else if (rank + percentMap.get(key).size() == room.getCurrentUserNum() + 1) {
					refund = 0;
				}
				findRankResultList.add(FindRankResult.findByRank(rankId++, rank, userInfo, refund));
			}
			rank += percentMap.get(key).size();
		}
		return findRankResultList;
	}

	@Override
	public void updateRoomInfo(RoomUpdateCommand command) {
		validateIsAdmin();

		// 방 소개 수정 - room
		Room room = roomRepository.findByRoomId(command.getRoomId()).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
		// category 정보
		CommonCode roomCategoryInfo = findRoomCategoryInfo(command.getRoomCategoryCode());
		// rule 정보 - 고정된 규칙
		CommonCode ruleInfo = findRuleInfo(command.getRuleCode());

		if (command.getMaxUserNum() < 1 || command.getMaxUserNum() < room.getCurrentUserNum()) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}
		// 방장 바꾸기
		Participant prevLeader = participantRepository.findLeaderByRoomId(room.getRoomId());

		if (prevLeader == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		prevLeader.updateAuthority(false);

		Participant newLeader = participantRepository.findByUserIdAndRoomId(command.getUserId(), room.getRoomId());

		if (newLeader == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		newLeader.updateAuthority(true);
		// if (!Objects.equals(command.getRoomCode(), room.getRoomCode()) && roomRepository.existByRoomCode(
		// 	command.getRoomCode())) {
		// 	throw new GotBetterException(MessageType.CONFLICT);
		// }
		// if (command.getWeek() < 1 || command.getWeek() < room.getCurrentWeek()) {
		// 	throw new GotBetterException(MessageType.BAD_REQUEST);
		// }
		// if (command.getEntryFee() < 0) {
		// 	throw new GotBetterException(MessageType.BAD_REQUEST);
		// }

		room.updateRoomInfo(
			command.getTitle(),
			command.getAccount(),
			command.getMaxUserNum(),
			roomCategoryInfo.getCommonCodeId().getCode(),
			ruleInfo.getCommonCodeId().getCode());
		roomRepository.save(room);
		participantRepository.save(prevLeader);
		participantRepository.save(newLeader);
	}

	@Override
	public void deleteRoom(Long roomId) {
		validateIsAdmin();

		Room room = roomRepository.findByRoomId(roomId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});

		roomRepository.deleteById(room.getRoomId());
	}

	/**
	 * other
	 */
	private String getRandomCode() {
		boolean useLetters = true;
		boolean useNumbers = true;
		int randomStrLen = 8;

		String roomCode;
		do {
			roomCode = RandomStringUtils.random(randomStrLen, useLetters, useNumbers);
		} while (roomRepository.existByRoomCode(roomCode));
		return roomCode;
	}

	private CommonCode findRoomCategoryInfo(String roomCategoryCode) {
		if (roomCategoryCode == null) { // 선택되지 않은 상태면 기본적으로 ETC로 설정
			roomCategoryCode = "ETC";
		}
		CommonCode roomCategoryInfo = commonCodeRepository.findByCommonCodeId(CommonCodeId.builder()
			.groupCode("ROOM_CATEGORY")
			.code(roomCategoryCode)
			.build());
		if (roomCategoryInfo == null) { // 카테고리 목록에 해당되는 것이 아니면 null 반환
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}
		return roomCategoryInfo;
	}

	private CommonCode findRuleInfo(String ruleCode) {
		if (ruleCode == null) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}
		CommonCode ruleInfo = commonCodeRepository.findByCommonCodeId(CommonCodeId.builder()
			.groupCode("RULE")
			.code(ruleCode)
			.build());
		if (ruleInfo == null) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}
		return ruleInfo;
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
}