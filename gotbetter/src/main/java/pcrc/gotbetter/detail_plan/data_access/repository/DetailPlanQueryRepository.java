package pcrc.gotbetter.detail_plan.data_access.repository;

import pcrc.gotbetter.detail_plan.data_access.dto.DetailPlanDto;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;

import java.util.HashMap;

public interface DetailPlanQueryRepository {

    Boolean existsByPlanId(Long planId);

    HashMap<String, Long> countCompleteTrue(Long planId);

    DetailPlan findByDetailPlanId(Long detailPlanId);

    // join
    DetailPlanDto findByDetailJoinRoom(Long detailPlanId);
}
