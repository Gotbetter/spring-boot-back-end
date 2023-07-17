package pcrc.gotbetter.plan.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.room.data_access.entity.Room;

@Getter
@AllArgsConstructor
public class PlanDto {
	private Plan plan;
	private Room room;
}
