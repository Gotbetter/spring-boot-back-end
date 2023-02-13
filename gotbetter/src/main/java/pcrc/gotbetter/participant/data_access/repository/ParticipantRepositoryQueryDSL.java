package pcrc.gotbetter.participant.data_access.repository;

import com.querydsl.core.Tuple;
import pcrc.gotbetter.participant.data_access.entity.Participant;
import pcrc.gotbetter.user.data_access.entity.User;

import java.util.List;

public interface ParticipantRepositoryQueryDSL {
    Participant findByUserIdAndRoomId(Long user_id, Long room_id);
    List<User> findWaitMembers(Long room_id);
    List<Tuple> findActiveMembers(Long room_id);
    Boolean existsWaitMemberInARoom(Long user_id, Long room_id, Boolean active);
    void updateParticipateAccepted(Long user_id, Long room_id);
}
/*
    Boolean existsRoomMatchLeaderId(Long leader_id, Long room_id);
    List<User> findMembersInARoom(Long room_id, Boolean accepted);
    void updateUserRoomAccepted(Long user_id, Long room_id);


* */