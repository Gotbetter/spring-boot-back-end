package pcrc.gotbetter.plan_evaluation.data_access.repository;

public interface PlanEvaluationRepositoryQueryDSL {
    // insert, update, delete
    void deletePlanEvaluation(Long planId, Long participantId);

    // select
    Boolean existsEval(Long planId, Long participantId);
}
