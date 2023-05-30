package pcrc.gotbetter.user.data_access.repository;

import pcrc.gotbetter.user.data_access.entity.User;

public interface UserRepositoryQueryDSL {
    // insert, update, delete
    void updateRefreshToken(Long userId, String refreshToken);
    void updateUsername(Long userId, String username);
    void updateFcmToken(Long userId, String fcmToken);

    // select
    Long findUserIdByEmail(String email);
    User findByEmail(String email);
}
