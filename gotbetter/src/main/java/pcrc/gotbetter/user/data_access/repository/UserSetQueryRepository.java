package pcrc.gotbetter.user.data_access.repository;

public interface UserSetQueryRepository {

	Boolean existsByUserId(Long userId);

	Boolean existsByAuthId(String authId);

	String findAuthIdByUserId(Long userId);

}
