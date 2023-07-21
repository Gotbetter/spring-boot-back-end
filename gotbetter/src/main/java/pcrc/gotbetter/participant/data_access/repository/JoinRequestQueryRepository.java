package pcrc.gotbetter.participant.data_access.repository;

import java.util.List;

import pcrc.gotbetter.participant.data_access.dto.JoinRequestDto;
import pcrc.gotbetter.participant.data_access.entity.JoinRequest;

public interface JoinRequestQueryRepository {

    JoinRequest findJoinRequest(Long userId, Long roomId);

    // join
    JoinRequestDto findJoinRequestJoin(Long userId, Long roomId, Boolean accepted);

    List<JoinRequestDto> findJoinRequestJoinList(Long userId, Long roomId, Boolean accepted);

}
