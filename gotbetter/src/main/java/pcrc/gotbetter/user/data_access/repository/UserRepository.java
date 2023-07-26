package pcrc.gotbetter.user.data_access.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.user.data_access.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {

	Optional<User> findByUserId(Long userId);

	User findByEmail(String email);

}
