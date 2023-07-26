package pcrc.gotbetter.plan.data_access.repository;

import java.util.HashMap;
import java.util.List;

import pcrc.gotbetter.plan.data_access.dto.PlanDto;
import pcrc.gotbetter.plan.data_access.entity.Plan;

public interface PlanQueryRepository {

	Plan findWeekPlanOfUser(Long participantId, Integer week);

	Boolean existsByParticipantId(Long participantId);

	List<HashMap<String, Object>> findPushNotification();

	// join
	PlanDto findPlanJoinRoom(Long planId);

	List<PlanDto> findPlanJoinParticipant(Long roomId, Integer passedWeek);

}
