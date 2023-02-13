package pcrc.gotbetter.plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pcrc.gotbetter.plan.data_access.entity.Plan;
import pcrc.gotbetter.plan.data_access.repository.PlanRepository;
import pcrc.gotbetter.room.data_access.entity.Room;
import pcrc.gotbetter.room.data_access.repository.RoomRepository;
import pcrc.gotbetter.setting.http_api.GotBetterException;
import pcrc.gotbetter.setting.http_api.MessageType;
import pcrc.gotbetter.user_room.data_access.repository.UserRoomRepository;

import java.util.ArrayList;
import java.util.List;

import static pcrc.gotbetter.setting.security.SecurityUtil.getCurrentUserId;

@Service
public class PlanService implements PlanOperationUseCase, PlanReadUseCase {
    private final PlanRepository planRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    @Autowired
    public PlanService(PlanRepository planRepository,
                       RoomRepository roomRepository, UserRoomRepository userRoomRepository) {
        this.planRepository = planRepository;
        this.roomRepository = roomRepository;
        this.userRoomRepository = userRoomRepository;
    }

    @Override
    public List<FindPlanResult> createPlans(PlanCreateCommand command) {
        validateLeaderIdOfRoom(command.getRoom_id());
        validateActiveUserInRoom(command.getRoom_id(), command.getUser_id());
        validateDuplicateCreatePlans(command);

        Room room = roomRepository.findByRoomId(command.getRoom_id())
                .orElseThrow(() -> {
                    throw new GotBetterException(MessageType.NOT_FOUND);
                });
        List<FindPlanResult> results = new ArrayList<>();

        for (int i = 1;i <= room.getWeek();i++) {
            Plan plan = Plan.builder()
                    .startDate(room.getStartDate().plusDays((i - 1) * 7L))
                    .targetDate(room.getStartDate().plusDays(i * 7L - 1))
                    .score(0.0F)
                    .week(i)
                    .threeDaysPassed(false)
                    .rejected(false)
                    .userId(command.getUser_id())
                    .roomId(command.getRoom_id())
                    .build();
            planRepository.save(plan);
            results.add(FindPlanResult.findByPlan(plan));
        }
        return results;
    }

    @Override
    public FindPlanResult getWeekPlan(PlanFindQuery query) {
        validateActiveUserInRoom(query.getRoom_id(), getCurrentUserId());

        Plan plan = planRepository.findWeekPlanOfUser(query).orElseThrow(() -> {
            throw new GotBetterException(MessageType.NOT_FOUND);
        });
        return FindPlanResult.findByPlan(plan);
    }

    /**
     * validate
     */
    private void validateLeaderIdOfRoom(Long room_id) {
        long user_id = getCurrentUserId();
        if (!userRoomRepository.existsRoomMatchLeaderId(user_id, room_id)) {
            throw new GotBetterException(MessageType.FORBIDDEN);
        }
    }
    private void validateActiveUserInRoom(Long room_id, Long user_id) {
        if (!userRoomRepository.existsMemberInARoom(room_id, user_id, true)) {
            throw new GotBetterException(MessageType.NOT_FOUND);
        }
    }

    private void validateDuplicateCreatePlans(PlanCreateCommand command) {
        if (planRepository.existsByRoomidAndUserid(command.getRoom_id(), command.getUser_id())) {
            throw new GotBetterException(MessageType.CONFLICT);
        }
    }
}
