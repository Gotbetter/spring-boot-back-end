package pcrc.gotbetter.detail_plan_record.service;

import static pcrc.gotbetter.setting.security.SecurityUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.detail_plan_record.data_access.dto.DetailPlanRecordDto;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanId;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;
import pcrc.gotbetter.detail_plan_record.data_access.repository.DetailPlanRecordRepository;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;

@Service
public class DetailPlanRecordService implements DetailPlanRecordOperationUseCase, DetailPlanRecordReadUseCase {

	private final DetailPlanRecordRepository detailPlanRecordRepository;
	private final DetailPlanRepository detailPlanRepository;
	private final PlanRepository planRepository;
	private final ParticipantRepository participantRepository;

	@Autowired
	public DetailPlanRecordService(DetailPlanRecordRepository detailPlanRecordRepository,
		DetailPlanRepository detailPlanRepository, PlanRepository planRepository,
		ParticipantRepository participantRepository) {
		this.detailPlanRecordRepository = detailPlanRecordRepository;
		this.detailPlanRepository = detailPlanRepository;
		this.planRepository = planRepository;
		this.participantRepository = participantRepository;
	}

	@Override
	public FindDetailPlanRecordResult createRecord(DetailPlanRecordCreateCommand command) {
		DetailPlan detailPlan = validateDetailPlan(command.getDetailPlanId());

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
			.recordPhoto(command.getRecordPhoto())
			.build();

		detailPlanRecordRepository.save(detailPlanRecord);
		return FindDetailPlanRecordResult.findByDetailPlanRecord(detailPlanRecord);
	}

	@Override
	public List<FindDetailPlanRecordResult> getRecordList(Long detailPlanId) {
		// 방에 속한 사용자인지 확인
		if (!participantRepository.existsByDetailPlanId(getCurrentUserId(), detailPlanId)) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		// 인증 리스트 조회
		List<DetailPlanRecord> records = detailPlanRecordRepository.findByDetailPlanIdDetailPlanId(detailPlanId);
		List<FindDetailPlanRecordResult> results = new ArrayList<>();

		for (DetailPlanRecord record : records) {
			results.add(FindDetailPlanRecordResult.findByDetailPlanRecord(record));
		}
		return results;
	}

	@Override
	public FindDetailPlanRecordResult updateRecord(DetailPlanRecordUpdateCommand command) {
		DetailPlanRecord detailPlanRecord = validateRecord(command.getRecordId(), command.getDetailPlanId());

		detailPlanRecord.updateRecord(command.getRecordTitle(), command.getRecordBody(), command.getRecordPhoto());
		detailPlanRecordRepository.save(detailPlanRecord);
		return FindDetailPlanRecordResult.findByDetailPlanRecord(detailPlanRecord);
	}

	@Override
	public void deleteRecord(DetailPlanRecordDeleteCommand command) {
		DetailPlanRecord detailPlanRecord = validateRecord(command.getRecordId(), command.getDetailPlanId());

		detailPlanRecordRepository.deleteById(detailPlanRecord.getRecordId());
	}

	/**
	 * validate
	 */
	private DetailPlan validateDetailPlan(Long detailPlanId) {
		DetailPlan detailPlan = detailPlanRepository.findByDetailPlanId(detailPlanId);

		if (detailPlan == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		validateAboutDetail(detailPlan);
		return detailPlan;
	}

	private DetailPlanRecord validateRecord(Long recordId, Long detailPlanId) {
		DetailPlanRecordDto detailPlanRecordDto = detailPlanRecordRepository.findDetailPlanJoinRecord(recordId);

		if (detailPlanRecordDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		DetailPlan detailPlan = detailPlanRecordDto.getDetailPlan();
		DetailPlanRecord detailPlanRecord = detailPlanRecordDto.getDetailPlanRecord();

		if (!Objects.equals(detailPlan.getDetailPlanId(), detailPlanId)) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		validateAboutDetail(detailPlan);
		return detailPlanRecord;
	}

	private void validateAboutDetail(DetailPlan detailPlan) {
		PlanDto planDto = planRepository.findPlanJoinRoom(detailPlan.getPlanId());

		if (planDto == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}
		// 인증할 수 있는 기간에 해당되는지 확인
		if (!validateDate(planDto.getPlan(), planDto.getRoom())) {
			throw new GotBetterException(MessageType.FORBIDDEN_DATE);
		}
		// 사용자가 세부 계획의 주인인지 확인
		// 계획 완료된 상태인지 확인
		// 세부 계획 평가가 불가능한 상태인지 확인
		if (!Objects.equals(detailPlan.getParticipantInfo().getUserId(), getCurrentUserId())
			|| detailPlan.getComplete()
			|| detailPlan.getRejected()) {
			throw new GotBetterException(MessageType.FORBIDDEN);
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
}
