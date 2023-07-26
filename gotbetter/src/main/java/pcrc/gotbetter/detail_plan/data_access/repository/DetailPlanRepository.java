package pcrc.gotbetter.detail_plan.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

@Repository
public interface DetailPlanRepository extends JpaRepository<DetailPlan, Long>, DetailPlanQueryRepository {
	List<DetailPlan> findByPlanId(Long planId);

	void deleteByPlanId(Long planId);

	void deleteByDetailPlanId(Long detailPlanId);
}
