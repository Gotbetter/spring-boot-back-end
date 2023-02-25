package pcrc.gotbetter.user.data_access.repository;

public interface UserRepositoryQueryDSL {
    // insert, update, delete
    void updateRefreshToken(String auth_id, String refresh_token);

    // select
    Boolean existsByAuthidOrEmail(String auth_id, String email);
}
