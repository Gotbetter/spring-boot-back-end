package pcrc.gotbetter.detail_plan.data_access.repository;

public interface DetailPlanRepositoryQueryDSL {
    void updateDetailContent(Long detail_plan_id, String content);
    void deleteDetailPlan(Long detail_plan_id);
}
