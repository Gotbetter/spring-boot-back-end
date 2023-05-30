package pcrc.gotbetter.setting;

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
import pcrc.gotbetter.push_notification.service.FCMOperationUseCase;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchNotification {
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	private FCMOperationUseCase fcmOperationUseCase;

	@Bean
	public Job pushNotificationsJob() {
		log.info("\">>>>> Run Push Notifications Job <<<<<\"");
		return new JobBuilder("pushNotificationsJob", jobRepository)
			.start(stepPushNotifications())
			.build();
	}

	private Step stepPushNotifications() {
		return new StepBuilder("stepPushNotifications", jobRepository)
			.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
				log.info("\">>> step_push_notifications <<<\"");

				// userRepository에서 token 가져오기
				// 진행중인 모임에 대해서 보내기
				fcmOperationUseCase.sendNotifications("d5GQ-hz_SJ-MQg11dRDYey:APA91bEfwaUyGH6KdlngekTkopl6os-X6wcbYPD_HL-I4vutD-biFaAut4nJOdvmMLdd0WmGclxKQ4oKi2U01BHS1ugvrF-bc_C68TPg2bkT-cntYkuYbldDegUgiCbuUefXOfrMdAFg", "테스트", "테스트한다");

				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}
}
