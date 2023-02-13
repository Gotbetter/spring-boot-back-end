package pcrc.gotbetter.plan.data_access.repository;

import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.util.Optional;

public interface PlanRepositoryQueryDSL {
    Boolean existsByParticipantId(Long participant_id);
    Optional<Plan> findWeekPlanOfUser(Long participant_id, Integer week);
}
