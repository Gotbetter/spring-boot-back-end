package pcrc.gotbetter.plan.data_access.repository;

import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.service.PlanReadUseCase;

import java.util.Optional;

public interface PlanRepositoryQueryDSL {
    Boolean existsByRoomidAndUserid(Long room_id, Long user_id);
    Optional<Plan> findWeekPlanOfUser(PlanReadUseCase.PlanFindQuery query);
}
