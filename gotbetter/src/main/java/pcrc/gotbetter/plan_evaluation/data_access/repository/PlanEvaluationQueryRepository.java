package pcrc.gotbetter.plan_evaluation.data_access.repository;

public interface PlanEvaluationQueryRepository {

    Boolean existsEval(Long planId, Long participantId);

}
