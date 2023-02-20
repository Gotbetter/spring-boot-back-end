package pcrc.gotbetter.plan_evaluation.data_access.repository;

public interface PlanEvaluationRepositoryQueryDSL {
//    void deleteDislike(Long plan_id, Long participant_id);
    Boolean existsEval(Long plan_id, Long participant_id);
}
