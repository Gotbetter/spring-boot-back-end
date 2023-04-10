package pcrc.gotbetter.detail_plan.data_access.repository;

import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.HashMap;
import java.util.Objects;

public interface DetailPlanRepositoryQueryDSL {
    // insert, update, delete
    void updateDetailContent(Long detail_plan_id, String content);
    void updateRejected(Long detail_plan_id, Boolean rejected);
    void updateDetailPlanCompleted(Long detail_plan_id, String approve_comment);
    void updateDetailPlanUndo(Long detail_plan_id, Boolean rejected);
    void deleteDetailPlan(Long detail_plan_id);

    // select
    Boolean existsByPlanId(Long plan_id);
    HashMap<String, Long> countCompleteTrue(Long plan_id);
    DetailPlan findByDetailPlanId(Long detail_plan_id);
}
