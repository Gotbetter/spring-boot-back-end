package pcrc.gotbetter.participant.data_access.repository;

import java.util.List;

public interface JoinRequestQueryRepository {

    JoinRequestDto findJoinRequest(Long userId, Long roomId, Boolean accepted);

    List<JoinRequestDto> findJoinRequestList(Long userId, Long roomId, Boolean accepted);

}
