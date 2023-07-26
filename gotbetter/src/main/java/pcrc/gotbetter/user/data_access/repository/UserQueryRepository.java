package pcrc.gotbetter.user.data_access.repository;

import java.util.HashMap;
import java.util.List;

public interface UserQueryRepository {

	HashMap<Long, List<String>> getAllUsersUserIdAndFcmToken();

}
