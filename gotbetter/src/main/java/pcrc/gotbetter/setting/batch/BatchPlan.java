package pcrc.gotbetter.setting.batch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchPlan {
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	private PlanRepository planRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private DetailPlanRepository detailPlanRepository;
	@Autowired
	private ParticipantRepository participantRepository;

	@Bean
	public Job job() {
		log.info("\">>>>> Run Job <<<<<\"");
		return new JobBuilder("job", jobRepository)
			.start(stepThreeDaysPassed())
			.next(stepWeekPassed())
			.next(stepSetRefund())
			.build();
	}

	private Step stepThreeDaysPassed() {
		return new StepBuilder("step_three_days_passed", jobRepository)
			.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

				log.info("\">>> (START) step_three_days_passed <<<\"");

				List<Plan> planList = planRepository.findByThreeDaysPassed(false);
				LocalDate now = LocalDate.now();

				for (Plan plan : planList) {
					LocalDate thirdDay = plan.getStartDate().plusDays(2L);

					if (thirdDay.isBefore(now)) {
						log.info("(change three days passed to TRUE |" +
							" roomId=" + plan.getParticipantInfo().getRoomId() +
							" planId=" + plan.getPlanId() +
							" startDate=" + plan.getStartDate() +
							")");
						plan.updateThreeDaysPassed();
						plan.updateById(RoleType.SERVER.getCode());
					}
				}
				planRepository.saveAll(planList);
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	private Step stepSetRefund() {
		return new StepBuilder("step_update_refund", jobRepository)
			.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

				log.info("\">>> (START) step_update_refund <<<\"");

				List<Room> roomList = roomRepository.findListLastWeek(); // 마지막 주
				LocalDate now = LocalDate.now();

				for (Room room : roomList) {
					LocalDate afterOneDayOfLastDay = room.getStartDate().plusDays(7L * room.getCurrentWeek());

					if (afterOneDayOfLastDay.isEqual(now)) { // 좀 더 고민
						updatePercentSum(room.getRoomId(), room.getCurrentWeek());
						updateRefund(room.getRoomId());
					}
				}
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	private Step stepWeekPassed() {
		return new StepBuilder("step_week_pass", jobRepository)
			.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

				log.info("\">>> (START) step_week_pass <<<\"");

				List<Room> roomList = roomRepository.findListUnderWeek();
				LocalDate now = LocalDate.now();

				for (Room room : roomList) {
					LocalDate lastDay = room.getStartDate().plusDays(7L * room.getCurrentWeek() - 1);

					if (lastDay.isBefore(now)) {
						int nextWeek = room.getCurrentWeek() + 1;

						log.info("(start to change score and percent sum |" + " roomId=" + room.getRoomId());
						updatePercentSum(room.getRoomId(), room.getCurrentWeek());
						room.updateCurrentWeekToNext();
						room.updateById(RoleType.SERVER.getCode());
						log.info("(change to next week |" +
							" roomId=" + room.getRoomId() +
							" startDate=" + room.getStartDate() +
							") new_week/total_week: " + nextWeek + "/" + room.getWeek());
					}
				}
				roomRepository.saveAll(roomList);
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	private void updatePercentSum(Long roomId, Integer passedWeek) {
		List<PlanDto> planDtoList = planRepository.findPlanJoinParticipant(roomId, passedWeek);
		List<Plan> resultPlans = new ArrayList<>();
		List<Participant> resultParticipants = new ArrayList<>();

		for (PlanDto planDto : planDtoList) {
			Plan plan = planDto.getPlan();
			Participant participant = planDto.getParticipant();

			if (!plan.getRejected()) {
				HashMap<String, Long> map = detailPlanRepository.countCompleteTrue(plan.getPlanId());
				Long size = map.get("size");
				Long completeCount = map.get("completeCount");

				float divide = size != 0 ? (float)completeCount / (float)size : 0;
				float percent = Math.round(divide * 1000) / 10.0F;

				plan.updateScore(percent);
				plan.updateById(RoleType.SERVER.getCode());
				participant.updatePercentSum(percent);
				participant.updateById(RoleType.SERVER.getCode());
				resultPlans.add(plan);
				resultParticipants.add(participant);
				log.info("(change the percent | participantId=" + plan.getParticipantInfo().getParticipantId() +
					") newPercent=" + percent);
			} else {
				log.info("(changing the percent is rejected | participantId=" +
					plan.getParticipantInfo().getParticipantId() + ")");
			}
			if (resultPlans.size() != 0) {
				planRepository.saveAll(resultPlans);
			}
			if (resultParticipants.size() != 0) {
				participantRepository.saveAll(resultParticipants);
			}
		}
	}

	private void updateRefund(Long roomId) {
		List<ParticipantDto> participantDtoList = participantRepository.findParticipantRoomByRoomId(roomId);

		if (participantDtoList.size() == 0) {
			return;
		}

		Room room = participantDtoList.get(0).getRoom();
		Map<Float, List<Participant>> percentMap = new HashMap<>();

		for (ParticipantDto participantDto : participantDtoList) {
			Participant participant = participantDto.getParticipant();
			Float key = participant.getPercentSum();
			List<Participant> participantList = (percentMap.containsKey(key)) ? percentMap.get(key) : new ArrayList<>();

			participantList.add(participant);
			percentMap.put(key, participantList);
		}

		List<Float> keySet = new ArrayList<>(percentMap.keySet());

		keySet.sort(Comparator.reverseOrder());

		int rank = 1;
		int leftRefund =
			keySet.size() == 1 ? 0 : room.getEntryFee() * percentMap.get(keySet.get(keySet.size() - 1)).size();

		for (Float key : keySet) {
			List<Participant> participants = percentMap.get(key);

			for (Participant participant : participants) {
				int refund = room.getEntryFee();

				if (rank == 1) {
					refund += (Math.floor(leftRefund) / participants.size());
				} else if (rank + percentMap.get(key).size() == room.getCurrentUserNum() + 1) {
					refund = 0;
				}
				participant.updateRefund(refund);
				participant.updateById(RoleType.SERVER.getCode());
				log.info("(will refund | participantId=" + participant.getParticipantId() + ") refund=" + refund);
			}
			participantRepository.saveAll(participants);
			rank += percentMap.get(key).size();
		}
	}
}
