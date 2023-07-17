package pcrc.gotbetter.plan.data_access.repository;

import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.util.HashMap;
import java.util.List;

public interface PlanQueryRepository {
    // insert, update, delete
    void updateRejected(Long planId, Boolean change);
    void updateThreeDaysPassed(Long planId);

    // select
    Plan findWeekPlanOfUser(Long participantId, Integer week);
    Boolean existsByParticipantId(Long participantId);
    List<Plan> findListByRoomId(Long roomId, Integer passedWeek);
    List<HashMap<String, Object>> findPushNotification();
}