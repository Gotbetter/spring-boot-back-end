package pcrc.gotbetter.setting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchScheduler {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private BatchConfig batchConfig;

    @Scheduled(cron = "0 0 0 * * *") // *(초) *(분) *(시) *(일) *(월) *(요일)
    public void runJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(batchConfig.job(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
        | JobParametersInvalidException | org.springframework.batch.core.repository.JobRestartException exception) {
            log.error(exception.getMessage());
        }
    }

//    @Scheduled(cron = "0 0/1 * * * *")
//    public void testRun() {
//        log.info(">>>>> TEST Job <<<<<");
//        batchConfig.update_refund(31L);
//    }
}
