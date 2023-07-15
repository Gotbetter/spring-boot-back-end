package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

public interface DetailPlanEvalQueryRepository {
    // insert, update, delete
    void deleteDetailPlanEval(Long detailPlanId, Long participantId);

    // select
    Boolean existsEval(Long detailPlanId, Long participantId);
}
