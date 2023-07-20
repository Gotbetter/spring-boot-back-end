package pcrc.gotbetter.plan.data_access.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.room.data_access.entity.Room;

@Getter
@AllArgsConstructor
public class PlanDto {
	private Plan plan;
	private Room room;
	private Participant participant;

	public PlanDto(Plan plan, Room room) {
		this.plan = plan;
		this.room = room;
	}

	public PlanDto(Plan plan, Participant participant) {
		this.plan = plan;
		this.participant = participant;
	}
}
