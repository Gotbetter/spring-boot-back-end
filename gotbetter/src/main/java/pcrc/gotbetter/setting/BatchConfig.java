package pcrc.gotbetter.setting;

import lombok.extern.slf4j.Slf4j;
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
import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {
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
                .next(stepWeekPass())
                .next(stepUpdateRefund())
                .build();
    }

    private Step stepThreeDaysPassed() {
        return new StepBuilder("step_three_days_passed", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    log.info("\">>> step_three_days_passed <<<\"");
                    List<Plan> planList = planRepository.findByThreeDaysPassed(false);
                    LocalDate now = LocalDate.now();
                    for (Plan plan : planList) {
                        LocalDate afterThreeDays = plan.getStartDate().plusDays(2L);
                        if (afterThreeDays.isBefore(now)) {
                            log.info("room_id: " + plan.getParticipantInfo().getRoomId()
                                    + ", plan_id: " + plan.getPlanId() + ", start_date: " + plan.getStartDate());
                            plan.updateThreeDaysPassed();
                        }
                    }
                    planRepository.saveAll(planList);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private Step stepWeekPass() {
        return new StepBuilder("step_week_pass", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    log.info("\">>> step_week_pass <<<\"");

                    List<Room> roomList = roomRepository.findListUnderWeek();
                    LocalDate now = LocalDate.now();

                    for (Room room : roomList) {
                        LocalDate lastDate = room.getStartDate().plusDays(7L * room.getCurrentWeek() - 1);
                        if (lastDate.isBefore(now)) {
                            updatePercentSum(room.getRoomId(), room.getCurrentWeek());
                            int nextWeek = room.getCurrentWeek() + 1;
                            room.updateCurrentWeekToNext();
                            log.info("room_id: " + room.getRoomId() + ", start_date: " + room.getStartDate()
                                    + ", current_week/week: " + nextWeek + "/" + room.getWeek());
                        }
                    }
                    roomRepository.saveAll(roomList);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private Step stepUpdateRefund() {
        return new StepBuilder("step_update_refund", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    log.info("\">>> step_update_refund <<<\"");

                    List<Room> roomList = roomRepository.findListLastWeek(); // 마지막 주
                    LocalDate now = LocalDate.now();

                    for (Room room : roomList) {
                        LocalDate lastDate = room.getStartDate().plusDays(7L * room.getCurrentWeek());
                        if (lastDate.isEqual(now)) {
                            updatePercentSum(room.getRoomId(), room.getCurrentWeek());
                            update_refund(room.getRoomId());
                        }
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private void updatePercentSum(Long roomId, Integer passedWeek) {
        log.info("\"update percent sum\"");

        List<PlanDto> planDtoList = planRepository.findPlanJoinParticipant(roomId, passedWeek);

        for (PlanDto planDto : planDtoList) {
            Plan plan = planDto.getPlan();
            Participant participant = planDto.getParticipant();

            if (!plan.getRejected()) {
                log.info("1. room_id: " + plan.getParticipantInfo().getRoomId()
                        + ", plan_id = " + plan.getPlanId() + ", current_week = " + plan.getWeek());
                HashMap<String, Long> map = detailPlanRepository.countCompleteTrue(plan.getPlanId());
                Long size = map.get("size");
                Long completeCount = map.get("completeCount");
                log.info("2. detail_size = " + size + ", complete_count = " + completeCount);

                if (size == 0 || completeCount == 0) {
                    continue;
                }
                float divide = (float) completeCount / (float) size;
                float percent = Math.round(divide * 1000) / 10.0F;
                log.info("3. divide = " + divide + ", percent = " + percent);
                log.info("4. participant_id = " + plan.getParticipantInfo().getParticipantId()
                        + ", plus percent = " + percent);
                participant.updatePercentSum(percent);
                participantRepository.save(participant);
            }
        }
    }

    private void update_refund(Long roomId) {
        log.info("\"update refund\"");
        List<ParticipantDto> participantDtoList = participantRepository.findParticipantRoomByRoomId(roomId);
        participantDtoList.sort((o1, o2) -> (int) (o2.getParticipant().getPercentSum() - o1.getParticipant().getPercentSum()));
        int rank = 1;

        for (ParticipantDto participantDto : participantDtoList) {
            Participant participant = participantDto.getParticipant();
            Room room = participantDto.getRoom();

            log.info("1. room_id: " + room.getRoomId() + ", participant_id = " + participant.getParticipantId()
                + ", current_week = " + room.getCurrentWeek());
            int refund = room.getEntryFee();
            if (rank == 1) {
                refund *= 2;
            } else if (room.getCurrentUserNum() == rank) {
                refund = 0;
            }
            log.info("2. participant_id = " + participant.getParticipantId() + ", refund = " + refund);
            participant.updateRefund(refund);
            participantRepository.save(participant);
            rank++;
        }
    }
}
