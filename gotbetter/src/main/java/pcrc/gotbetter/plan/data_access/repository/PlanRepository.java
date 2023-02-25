package pcrc.gotbetter.plan.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.plan.data_access.entity.Plan;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryQueryDSL {
    Optional<Plan> findByPlanId(Long plan_id);
    List<Plan> findByThreeDaysPassed(Boolean three_days_passed);
}
