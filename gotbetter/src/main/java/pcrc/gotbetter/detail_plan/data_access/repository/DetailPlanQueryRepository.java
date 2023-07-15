package pcrc.gotbetter.detail_plan.data_access.repository;

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.HashMap;

public interface DetailPlanQueryRepository {
    // insert, update, delete
    void updateDetailContent(Long detailPlanId, String content);
    void updateRejected(Long detailPlanId, Boolean rejected);
    void updateDetailPlanCompleted(Long detailPlanId, String approveComment);
    void updateDetailPlanUndo(Long detailPlanId, Boolean rejected);
    void deleteDetailPlan(Long detailPlanId);

    // select
    Boolean existsByPlanId(Long planId);
    HashMap<String, Long> countCompleteTrue(Long planId);
    DetailPlan findByDetailPlanId(Long detailPlanId);
}
