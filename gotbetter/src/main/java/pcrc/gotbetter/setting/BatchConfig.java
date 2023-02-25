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
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;

import java.time.LocalDate;
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

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .start(step_three_days_passed())
                .next(step_week_pass())
                .build();
    }

    private Step step_three_days_passed() {
        return new StepBuilder("step_three_days_passed", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    // get plan all list
                    // if three_days_passed == false
                    //      if now <= start_date + 3
                    //          update three_days_passed = true
                    List<Plan> planList = planRepository.findByThreeDaysPassed(false);
                    System.out.println(">>> plan_count: " + planList.size());
                    LocalDate now = LocalDate.now();
                    for (Plan plan : planList) {
                        LocalDate afterThreeDays = plan.getStartDate().plusDays(2L);
                        if (afterThreeDays.isBefore(now)) {
                            System.out.println("plan_id: " + plan.getPlanId() + ", start_date: " + plan.getStartDate()
                                    + ", three_days_passed: " + plan.getThreeDaysPassed());
                            planRepository.updateThreeDaysPassed(plan.getPlanId());
                        }
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private Step step_week_pass() {
        return new StepBuilder("step_week_pass", jobRepository)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    // get room all list
                    // if week < current_date
                    //      if now <= startdate + 7
                    //          update current_week++
                    List<Room> roomList = roomRepository.findListUnderWeek();
                    System.out.println(">>> room_count: " + roomList.size());
                    LocalDate now = LocalDate.now();
                    for (Room room : roomList) {
                        LocalDate weekPassed;
                        int plusWeek = room.getCurrentWeek();
                        while (true) {
                            weekPassed = room.getStartDate().plusDays(7L * plusWeek - 1);
                            if (!weekPassed.isBefore(now)) {
                                break;
                            }
                            plusWeek++;
                        }
                        if (plusWeek != room.getCurrentWeek()) {
                            System.out.println("room_id: " + room.getRoomId() + ", start_date: " + room.getStartDate()
                                    + ", current_week/week: " + room.getCurrentWeek() + "/" + room.getWeek());
                            roomRepository.updateCurrentWeek(room.getRoomId(), plusWeek);
                        }
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
