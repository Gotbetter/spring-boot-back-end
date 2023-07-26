package pcrc.gotbetter.user.data_access.repository;

import pcrc.gotbetter.user.data_access.entity.SocialAccount;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;

public interface SocialAccountQueryRepository {

	SocialAccount findByTypeAndId(ProviderType providerType, String providerId);

}
