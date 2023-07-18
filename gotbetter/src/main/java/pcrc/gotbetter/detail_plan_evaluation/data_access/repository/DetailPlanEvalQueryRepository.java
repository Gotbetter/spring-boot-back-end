package pcrc.gotbetter.detail_plan_evaluation.data_access.repository;

public interface DetailPlanEvalQueryRepository {

    Boolean existsEval(Long detailPlanId, Long participantId);

}
