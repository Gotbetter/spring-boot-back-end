package pcrc.gotbetter.setting.common;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pcrc.gotbetter.detail_plan.data_access.repository.DetailPlanRepository;
import pcrc.gotbetter.participant.data_access.dto.ParticipantDto;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.participant.data_access.repository.ParticipantRepository;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Component
public class TaskResult {

	private final ParticipantRepository participantRepository;

	private final PlanRepository planRepository;

	private final DetailPlanRepository detailPlanRepository;

	@Autowired
	public TaskResult(ParticipantRepository participantRepository, PlanRepository planRepository,
		DetailPlanRepository detailPlanRepository) {
		this.participantRepository = participantRepository;
		this.planRepository = planRepository;
		this.detailPlanRepository = detailPlanRepository;
	}

	public void updateScore(Long planId) {
		Plan plan = planRepository.findByPlanId(planId).orElseThrow(() -> {
			throw new GotBetterException(MessageType.NOT_FOUND);
		});
		LocalDate now = LocalDate.now();

		if (!now.isAfter(plan.getTargetDate())) {
			return;
		}
		Participant participant = participantRepository.findByParticipantId(
			plan.getParticipantInfo().getParticipantId());

		if (participant == null) {
			throw new GotBetterException(MessageType.NOT_FOUND);
		}

		HashMap<String, Long> map = detailPlanRepository.countCompleteTrue(plan.getPlanId());
		Long size = map.get("size");
		Long completeCount = map.get("completeCount");

		float divide = size != 0 ? (float)completeCount / (float)size : 0;
		float percent = Math.round(divide * 1000) / 10.0F;
		Float prevScore = plan.getScore();

		plan.updateScore(percent);
		planRepository.save(plan);

		participant.updatePercentSum(-prevScore + percent);
		participantRepository.save(participant);
	}

	public void updateRefund(Long roomId) {
		List<ParticipantDto> participantDtoList = participantRepository.findParticipantRoomByRoomId(roomId);

		if (participantDtoList.size() == 0) {
			return;
		}

		Room room = participantDtoList.get(0).getRoom();
		Map<Float, List<Participant>> percentMap = new HashMap<>();

		for (ParticipantDto participantDto : participantDtoList) {
			Participant participant = participantDto.getParticipant();
			Float key = participant.getPercentSum();
			List<Participant> participantList = (percentMap.containsKey(key)) ? percentMap.get(key) : new ArrayList<>();

			participantList.add(participant);
			percentMap.put(key, participantList);
		}

		List<Float> keySet = new ArrayList<>(percentMap.keySet());

		keySet.sort(Comparator.reverseOrder());

		int rank = 1;

		for (Float key : keySet) {
			List<Participant> participants = percentMap.get(key);

			for (Participant participant : participants) {
				int refund = room.getEntryFee();

				if (rank == 1) {
					if (room.getCurrentWeek() == 1) {
						refund = 0;
					} else {
						refund *= 2;
					}
				} else if (rank + percentMap.get(key).size() == room.getCurrentUserNum() + 1) {
					refund = 0;
				}
				participant.updateRefund(refund);
				participant.updateById(RoleType.SERVER.getCode());
			}
			participantRepository.saveAll(participants);
			rank += percentMap.get(key).size();
		}
	}
}
