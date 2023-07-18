package pcrc.gotbetter.detail_plan.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.detail_plan.data_access.entity.DetailPlan;
import pcrc.gotbetter.room.data_access.entity.Room;

@Getter
@AllArgsConstructor
public class DetailPlanDto {
	private DetailPlan detailPlan;
	private Room room;
}
