package pcrc.gotbetter.user.data_access.repository;

import pcrc.gotbetter.user.login_method.login_type.ProviderType;

public interface SocialAccountRepositoryQueryDSL {
    // create, update, delete

    // select
    Boolean existsByProviderTypeAndProviderId(ProviderType providerType, String providerId);
}
