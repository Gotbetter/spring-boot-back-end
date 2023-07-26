package pcrc.gotbetter.setting.batch;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

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
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.push_notification.service.FCMOperationUseCase;
import pcrc.gotbetter.user.data_access.repository.UserRepository;

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
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PlanRepository planRepository;

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
				// userId와 토큰 맵 가져오기
				HashMap<Long, List<String>> users = userRepository.getAllUsersUserIdAndFcmToken();
				// System.out.println(users);
				// room 테이블에서 시간 지난거는 제외
				// roomId와 current week로 plan 리스트 가져오기
				List<HashMap<String, Object>> info = planRepository.findPushNotification();
				// System.out.println(planRepository.findPushNotification());
				for (HashMap<String, Object> data : info) {
					Boolean threeDaysPassed = (Boolean)data.get("threeDaysPassed");
					// three days passed인지 아닌지 확인
					if (!threeDaysPassed) {
						pushForWritePlan(data, users);
					} else {
						pushForCompletePlan(data, users);
					}
				}
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	public void pushForWritePlan(HashMap<String, Object> data, HashMap<Long, List<String>> users) throws IOException {
		// 3일차인지 확인 후 true면 알림 전송
		LocalDate planStartDate = LocalDate.parse(data.get("planStartDate").toString());
		LocalDate threeDay = planStartDate.plusDays(2L);
		LocalDate now = LocalDate.now();
		if (threeDay.isEqual(now)) {
			String username = users.get((Long)data.get("userId")).get(0);
			String fcmToken = users.get((Long)data.get("userId")).get(1);
			String title = "\uD83D\uDEA8\uD83D\uDEA8 계획 작성 마감 임박 \uD83D\uDEA8\uD83D\uDEA8";
			String body = username + "님 오늘은 \"" + data.get("title") + "\" 방의 계획 작성 마감일입니다!";
			// System.out.println(title);
			// System.out.println(body);
			fcmOperationUseCase.sendNotifications(fcmToken, title, body);
		}
	}

	public void pushForCompletePlan(HashMap<String, Object> data, HashMap<Long, List<String>> users) throws
		IOException {
		// 7일차인지 확인 후 true면 알림 전송
		LocalDate planStartDate = LocalDate.parse(data.get("planStartDate").toString());
		LocalDate lastDay = planStartDate.plusDays(6L);
		LocalDate now = LocalDate.now();
		if (lastDay.isEqual(now)) {
			String username = users.get((Long)data.get("userId")).get(0);
			String fcmToken = users.get((Long)data.get("userId")).get(1);
			String title = "\uD83D\uDEA8\uD83D\uDEA8 계획 완료 마감 임박 \uD83D\uDEA8\uD83D\uDEA8";
			String body = username + "님 오늘은 \"" + data.get("title") + "\" 방의 계획 완료 마감일입니다!";
			// System.out.println(title);
			// System.out.println(body);
			fcmOperationUseCase.sendNotifications(fcmToken, title, body);
		}
	}
}