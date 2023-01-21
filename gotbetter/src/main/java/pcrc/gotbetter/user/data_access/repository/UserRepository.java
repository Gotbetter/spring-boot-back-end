package pcrc.gotbetter.user.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.user.data_access.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryQueryDSL {
    Optional<User> findByAuthId(String auth_id);
}