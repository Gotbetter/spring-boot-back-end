package pcrc.gotbetter.plan_evaluation.data_access.repository;

public interface PlanEvaluationRepositoryQueryDSL {
    Boolean existsEval(Long plan_id, Long participant_id);
    void deletePlanEvaluation(Long plan_id, Long participant_id);
}
