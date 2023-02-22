package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

public interface DetailPlanEvalRepositoryQueryDSL {
    Boolean existsEval(Long detail_plan_id, Long participant_id);
}
