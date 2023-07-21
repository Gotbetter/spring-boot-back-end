package pcrc.gotbetter.detail_plan_record.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.detail_plan_record.data_access.entity.DetailPlanRecord;

@Getter
@AllArgsConstructor
public class DetailPlanRecordDto {

	private DetailPlanRecord detailPlanRecord;

	private DetailPlan detailPlan;

}
