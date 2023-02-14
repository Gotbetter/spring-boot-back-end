package pcrc.gotbetter.detail_plan.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailPlanRepository extends JpaRepository<DetailPlan, Long>, DetailPlanRepositoryQueryDSL {
    List<DetailPlan> findByPlanId(Long plan_id);
    Optional<DetailPlan> findByDetailPlanId(Long detail_plan_id);
}
