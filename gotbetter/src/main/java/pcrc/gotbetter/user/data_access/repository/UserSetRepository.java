package pcrc.gotbetter.user.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pcrc.gotbetter.user.data_access.entity.UserSet;

@Repository
public interface UserSetRepository extends JpaRepository<UserSet, Long>, UserSetQueryRepository {

	UserSet findByAuthId(String authId);

	UserSet findByUserId(Long userId);

}
