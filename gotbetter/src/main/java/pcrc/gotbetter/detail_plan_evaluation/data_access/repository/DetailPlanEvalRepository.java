package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEval;
import pcrc.gotbetter.detail_plan_evaluation.data_access.entity.DetailPlanEvalId;

import java.util.List;

@Repository
public interface DetailPlanEvalRepository
        extends JpaRepository<DetailPlanEval, DetailPlanEvalId>, DetailPlanEvalRepositoryQueryDSL {
    List<DetailPlanEval> findByDetailPlanEvalIdDetailPlanId(Long detail_plan_id);
    void deleteByDetailPlanEvalId(DetailPlanEvalId detailPlanEvalId);
    void deleteByDetailPlanEvalIdDetailPlanId(Long detail_plan_id);
}
