package pcrc.gotbetter.user.data_access.repository;

import java.util.HashMap;
import java.util.List;

public interface UserRepositoryQueryDSL {
    // insert, update, delete
    void updateRefreshToken(Long userId, String refreshToken);
    void updateFcmToken(Long userId, String fcmToken);

    // select
    HashMap<Long, List<String>> getAllUsersUserIdAndFcmToken();
}
