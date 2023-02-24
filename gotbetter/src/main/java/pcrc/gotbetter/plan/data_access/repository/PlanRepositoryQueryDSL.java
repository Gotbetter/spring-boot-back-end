package pcrc.gotbetter.plan.data_access.repository;

import pcrc.gotbetter.plan.data_access.entity.Plan;

public interface PlanRepositoryQueryDSL {
    // insert, update, delete
    void updateRejected(Long plan_id, Boolean change);
    void updateThreeDaysPassed(Long plan_id);

    // select
    Plan findWeekPlanOfUser(Long participant_id, Integer week);
    Boolean existsByParticipantId(Long participant_id);
    Boolean existsByThreeDaysPassed(Long plan_id);

}
