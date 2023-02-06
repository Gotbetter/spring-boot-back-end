package pcrc.gotbetter.user_room.data_access.repository;

import pcrc.gotbetter.user.data_access.entity.User;

import java.util.List;

public interface UserRoomRepositoryQueryDSL {
    List<User> findMembersInARoom(Long room_id, Boolean accepted);
    Boolean existsRoomMatchLeaderId(Long leader_id, Long room_id);
    Boolean existsActiveMemberInARoom(Long room_id, Long user_id);
}
