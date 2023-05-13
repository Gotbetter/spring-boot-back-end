package pcrc.gotbetter.user.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.user.data_access.entity.SocialAccount;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long>, SocialAccountRepositoryQueryDSL {
}
