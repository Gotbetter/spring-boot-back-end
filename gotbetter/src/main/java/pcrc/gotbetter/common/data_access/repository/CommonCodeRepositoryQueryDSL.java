package pcrc.gotbetter.common.data_access.repository;

import pcrc.gotbetter.common.data_access.entity.CommonCode;
import pcrc.gotbetter.common.data_access.entity.CommonCodeId;

import java.util.List;

public interface CommonCodeRepositoryQueryDSL {
    // select
    Boolean existsByCommonCodeId(CommonCodeId commonCodeId);
    CommonCode findRoomCategoryInfo(String roomCategory);
    List<CommonCode> findRoomCategories();
}
