package pcrc.gotbetter.user.data_access.repository;

public interface UserRepositoryQueryDSL {
    // insert, update, delete
    void updateRefreshToken(String auth_id, String refresh_token);
    void updateUsername(Long userId, String username);

    // select
    Long findUserIdByEmail(String email);
}
