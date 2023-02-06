package pcrc.gotbetter.user.data_access.repository;

public interface UserRepositoryQueryDSL {
    Boolean existsByAuthidOrEmail(String auth_id, String email);
    void updateRefreshToken(String auth_id, String refresh_token);
}
