package pcrc.gotbetter.user.data_access.repository;

public interface UserSetRepositoryQueryDSL {

    Boolean existsByUserId(Long userId);

    Boolean existsByAuthId(String authId);

    String findAuthIdByUserId(Long userId);

}
