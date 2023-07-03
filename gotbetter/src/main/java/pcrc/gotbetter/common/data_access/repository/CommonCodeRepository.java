package pcrc.gotbetter.common.data_access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, CommonCodeId>, CommonCodeRepositoryQueryDSL {

}
