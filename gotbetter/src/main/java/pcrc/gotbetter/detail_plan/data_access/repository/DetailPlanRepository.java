package pcrc.gotbetter.detail_plan.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.List;

@Repository
public interface DetailPlanRepository extends JpaRepository<DetailPlan, Long>, DetailPlanQueryRepository {
    List<DetailPlan> findByPlanId(Long planId);

    void deleteByPlanId(Long planId);

    void deleteByDetailPlanId(Long detailPlanId);
}
