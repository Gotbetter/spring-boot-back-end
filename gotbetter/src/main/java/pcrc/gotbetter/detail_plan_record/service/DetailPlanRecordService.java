package pcrc.gotbetter.detail_plan_record.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.repository.DetailPlanEvalRepository;
import pcrc.gotbetter.detail_plan_record.data_access.dto.DetailPlanRecordDto;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanId;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;
import pcrc.gotbetter.detail_plan_record.data_access.repository.DetailPlanRecordRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.common.TaskResult;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.data_access.entity.User;
import pcrc.gotbetter.user.data_access.repository.UserRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Service
public class DetailPlanRecordService implements DetailPlanRecordOperationUseCase, DetailPlanRecordReadUseCase {
	@Value("${local.default.record.path}")
	String RECORD_LOCAL_PATH;
	@Value("${server.default.record.path}")
	String RECORD_SERVER_PATH;
	@Value("${local.default.loading.image}")
	String RECORD_LOCAL_DEFAULT_IMG;
	@Value("${server.default.loading.image}")
	String RECORD_SERVER_DEFAULT_IMG;
	private final DetailPlanRecordRepository detailPlanRecordRepository;
	private final DetailPlanRepository detailPlanRepository;
	private final PlanRepository planRepository;
	private final ParticipantRepository participantRepository;
	private final UserRepository userRepository;
	private final DetailPlanEvalRepository detailPlanEvalRepository;
	private final TaskResult taskResult;

	@Autowired
	public DetailPlanRecordService(
		DetailPlanRecordRepository detailPlanRecordRepository,
		DetailPlanRepository detailPlanRepository, PlanRepository planRepository,
		ParticipantRepository participantRepository, UserRepository userRepository,
		DetailPlanEvalRepository detailPlanEvalRepository, TaskResult taskResult
	) {
		this.detailPlanRecordRepository = detailPlanRecordRepository;
		this.detailPlanRepository = detailPlanRepository;
		this.planRepository = planRepository;
		this.participantRepository = participantRepository;
		this.userRepository = userRepository;
		this.detailPlanEvalRepository = detailPlanEvalRepository;
		this.taskResult = taskResult;
	}

	@Override
	public FindDetailPlanRecordResult createRecord(DetailPlanRecordCreateCommand command) {
		if (command.getAdmin()) {
			validateIsAdmin();
		}
		DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId(), command.getAdmin());

		if (detailPlan.getRejected()) {
			detailPlan.updateRejected(false);
			detailPlanRepository.save(detailPlan);
		}
		// 계획 인증 생성
		DetailPlanRecord detailPlanRecord = DetailPlanRecord.builder()
			.detailPlanId(DetailPlanId.builder()
				.detailPlanId(detailPlan.getDetailPlanId())
				.planId(detailPlan.getPlanId())
				.participantId(detailPlan.getParticipantInfo().getParticipantId())
				.userId(detailPlan.getParticipantInfo().getUserId())
				.roomId(detailPlan.getParticipantInfo().getRoomId())
				.build())
			.recordTitle(command.getRecordTitle())
			.recordBody(command.getRecordBody())
			.recordPhoto("to be continued")
			.build();

		detailPlanRecordRepository.save(detailPlanRecord);
		// 이미지
		String bytes = storePhoto(detailPlanRecord, null, null, command.getAdmin() ? null : command.getRecordPhoto());
		return FindDetailPlanRecordResult.findByDetailPlanRecord(detailPlanRecord, bytes, command.getAdmin());
	}

	@Override
	public List<FindDetailPlanRecordResult> getRecordList(RecordsFindQuery query) throws IOException {
		if (query.getAdmin()) {
			validateIsAdmin();
		} else {
			// 방에 속한 사용자인지 확인
			if (!participantRepository.existsByDetailPlanId(getCurrentUserId(), query.getDetailPlanId())) {
				throw new GotBetterException(MessageType.NOT_FOUND);
			}
		}

		// 인증 리스트 조회
		List<DetailPlanRecord> records = detailPlanRecordRepository.findByDetailPlanIdDetailPlanId(
			query.getDetailPlanId());
		List<FindDetailPlanRecordResult> results = new ArrayList<>();
		String os = System.getProperty("os.name").toLowerCase();

		for (DetailPlanRecord record : records) {

			String bytes;

			try {
				bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(
					Paths.get(record.getRecordPhoto())));
			} catch (Exception e) {
				String dir = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
				bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
			}
			results.add(FindDetailPlanRecordResult.findByDetailPlanRecord(record, bytes, query.getAdmin()));
		}
		return results;
	}

	@Override
	public FindDetailPlanRecordResult updateRecord(DetailPlanRecordUpdateCommand command) {
		if (command.getAdmin()) {
			validateIsAdmin();
		}
		DetailPlanRecord detailPlanRecord = validateRecord(command.getRecordId(), command.getDetailPlanId(),
			command.getAdmin());
		String bytes = storePhoto(detailPlanRecord, command.getRecordTitle(), command.getRecordBody(),
			command.getAdmin() ? null : command.getRecordPhoto());
		return FindDetailPlanRecordResult.findByDetailPlanRecord(detailPlanRecord, bytes, command.getAdmin());
	}

	@Override
	@Transactional
	public void deleteRecord(DetailPlanRecordDeleteCommand command) {
		if (command.getAdmin()) {
			validateIsAdmin();
		}

		DetailPlanRecord detailPlanRecord = validateRecord(command.getRecordId(), command.getDetailPlanId(),
			command.getAdmin());

		try {
			String os = System.getProperty("os.name").toLowerCase();
			String defaultDir = os.contains("win") ? RECORD_LOCAL_PATH : RECORD_SERVER_PATH;

			if (!command.getAdmin()) {
				String photoDir = defaultDir + "/" + detailPlanRecord.getDetailPlanId().getDetailPlanId();

				deleteImages(photoDir, detailPlanRecord.getRecordId(), true);
			}
		} catch (Exception e) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}

		DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(
			detailPlanRecord.getDetailPlanId().getDetailPlanId());

		if (detailPlan == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		detailPlanRecordRepository.deleteById(detailPlanRecord.getRecordId());
		if (command.getAdmin()) {
			List<DetailPlanRecord> detailPlanRecords = detailPlanRecordRepository.findByDetailPlanIdDetailPlanId(
				detailPlan.getDetailPlanId());

			if (detailPlanRecords.size() == 0) {
				detailPlanEvalRepository.deleteByDetailPlanEvalIdDetailPlanId(detailPlan.getDetailPlanId());
				detailPlan.updateDetailPlanUndo(false);
				detailPlanRepository.save(detailPlan);
			}
			taskResult.updateScore(detailPlan.getPlanId());
			taskResult.updateRefund(detailPlan.getParticipantInfo().getRoomId());
		}
	}

	/**
	 * validate
	 */
	private DetailPlan validateDetailPlan(Long detailPlanId, Boolean admin) {
		DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detailPlanId);

		if (detailPlan == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		validateAboutDetail(detailPlan, admin);
		// 계획 완료된 상태인지 확인
		if (!admin) {
			if (detailPlan.getComplete()) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
		}
		return detailPlan;
	}

	private DetailPlanRecord validateRecord(Long recordId, Long detailPlanId, Boolean admin) {
		DetailPlanRecordDto detailPlanRecordDto = detailPlanRecordRepository.findDetailPlanJoinRecord(recordId);

		if (detailPlanRecordDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		DetailPlan detailPlan = detailPlanRecordDto.getDetailPlan();
		DetailPlanRecord detailPlanRecord = detailPlanRecordDto.getDetailPlanRecord();

		if (!Objects.equals(detailPlan.getDetailPlanId(), detailPlanId)) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		validateAboutDetail(detailPlan, admin);
		// 계획 완료된 상태인지 확인
		// 세부 계획 평가가 불가능한 상태인지 확인
		if (!admin) {
			if (detailPlan.getComplete() || detailPlan.getRejected()) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
		}
		return detailPlanRecord;
	}

	private void validateAboutDetail(DetailPlan detailPlan, Boolean admin) {
		PlanDto planDto = planRepository.findPlanJoinRoom(detailPlan.getPlanId());

		if (planDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		if (!admin) {
			// 인증할 수 있는 기간에 해당되는지 확인
			if (!validateDate(planDto.getPlan(), planDto.getRoom())) {
				throw new GotBetterException(MessageType.FORBIDDEN_DATE);
			}
			// 사용자가 세부 계획의 주인인지 확인
			if (!Objects.equals(detailPlan.getParticipantInfo().getUserId(), getCurrentUserId())) {
				throw new GotBetterException(MessageType.FORBIDDEN);
			}
		}
	}

	private Boolean validateDate(Plan plan, Room room) {
		if (!Objects.equals(room.getCurrentWeek(), plan.getWeek())) {
			return false;
		} else {
			return !plan.getStartDate().isAfter(LocalDate.now())
				&& !plan.getTargetDate().isBefore(LocalDate.now());
		}
	}

	private String storePhoto(
		DetailPlanRecord detailPlanRecord,
		String recordTitle,
		String recordBody,
		MultipartFile photo
	) {
		String os = System.getProperty("os.name").toLowerCase();
		String defaultDir = os.contains("win") ? RECORD_LOCAL_PATH : RECORD_SERVER_PATH;
		String bytes;

		try {
			String fileName;
			if (photo != null) {
				String photoDir = defaultDir + "/" + detailPlanRecord.getDetailPlanId().getDetailPlanId();
				String extension = Objects.requireNonNull(photo.getOriginalFilename())
					.substring(photo.getOriginalFilename().lastIndexOf(".") + 1);

				fileName = photoDir + "/" + detailPlanRecord.getRecordId() + "." + extension;
				bytes = Base64.getEncoder().encodeToString(IOUtils.toByteArray(photo.getInputStream()));
				// 저장소에 사진 저장
				deleteImages(photoDir, detailPlanRecord.getRecordId(), false);
				photo.transferTo(new File(fileName));
			} else {
				if (Objects.equals(detailPlanRecord.getRecordPhoto(), "to be continued")) {
					fileName = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
					bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(fileName)));
					fileName = RECORD_SERVER_DEFAULT_IMG;
				} else {
					fileName = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
					bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(fileName)));
					fileName = detailPlanRecord.getRecordPhoto();
				}
			}
			// 경로 수정
			String updateRecordTitle = recordTitle == null ? detailPlanRecord.getRecordTitle() : recordTitle;
			String updateRecordBody = recordTitle == null ? detailPlanRecord.getRecordBody() : recordBody;
			detailPlanRecord.updateRecord(updateRecordTitle, updateRecordBody, fileName);
			detailPlanRecordRepository.save(detailPlanRecord);
		} catch (Exception e) {
			throw new GotBetterException(MessageType.BAD_REQUEST);
		}
		return bytes;
	}

	private void deleteImages(String photoDir, Long recordId, Boolean forDelete) {
		// 저장소에 사진 저장
		File storeDir = new File(photoDir);

		if (!forDelete && !storeDir.exists()) {
			try {
				storeDir.mkdirs();
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else {
			String[] files = storeDir.list();

			if (files == null) {
				return;
			}
			for (String file : files) {
				if (file.startsWith(recordId + ".")) {
					File image = new File(photoDir + "/" + file);
					image.delete();
					if (forDelete && files.length == 1) {
						File dir = new File(photoDir);
						dir.delete();
					}
				}
			}

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
