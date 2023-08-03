package pcrc.gotbetter.user.data_access.repository;

import java.util.HashMap;
import java.util.List;

import pcrc.gotbetter.user.data_access.dto.UserDto;

public interface UserQueryRepository {

	HashMap<Long, List<String>> getAllUsersUserIdAndFcmToken();

	List<UserDto> findAllUserUserSet();

}
