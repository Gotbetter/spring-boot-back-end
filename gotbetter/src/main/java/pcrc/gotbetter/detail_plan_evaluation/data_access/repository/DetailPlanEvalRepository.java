package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEvalId;

@Repository
public interface DetailPlanEvalRepository
	extends JpaRepository<DetailPlanEval, DetailPlanEvalId>, DetailPlanEvalQueryRepository {
	List<DetailPlanEval> findByDetailPlanEvalIdDetailPlanId(Long detailPlanId);

	Integer countByDetailPlanEvalIdDetailPlanId(Long detailPlanId);

	void deleteByDetailPlanEvalIdDetailPlanId(Long detailPlanId);

	void deleteByDetailPlanEvalId(DetailPlanEvalId detailPlanEvalId);
}
