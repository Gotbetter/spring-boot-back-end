package pcrc.gotbetter.plan.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryQueryDSL {
    Optional<Plan> findByPlanId(Long planId);
    List<Plan> findByThreeDaysPassed(Boolean threeDaysPassed);
}
