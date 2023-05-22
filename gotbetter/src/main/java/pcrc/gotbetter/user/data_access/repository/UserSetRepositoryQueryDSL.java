package pcrc.gotbetter.user.data_access.repository;

import pcrc.gotbetter.user.data_access.entity.UserSet;

public interface UserSetRepositoryQueryDSL {
    // insert, update, delete

    // select
    Boolean existsByUserId(Long userId);
    Boolean existsByAuthId(String authId);
    UserSet findByUserId(Long userId);
    UserSet findByAuthId(String authId);
    String findAuthIdByUserId(Long userId);
}
