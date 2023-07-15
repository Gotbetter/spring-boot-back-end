package pcrc.gotbetter.common.data_access.repository;

import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;

import java.util.List;

public interface CommonCodeQueryRepository {

    CommonCode findByCommonCodeId(CommonCodeId commonCodeId);

    List<CommonCode> findListByGroupCode(String groupCode);

}
