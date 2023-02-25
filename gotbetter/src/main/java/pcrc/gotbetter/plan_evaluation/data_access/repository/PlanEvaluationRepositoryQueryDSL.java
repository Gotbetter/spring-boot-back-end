package pcrc.gotbetter.plan_evaluation.data_access.repository;

public interface PlanEvaluationRepositoryQueryDSL {
    // insert, update, delete
    void deletePlanEvaluation(Long plan_id, Long participant_id);

    // select
    Boolean existsEval(Long plan_id, Long participant_id);
}
