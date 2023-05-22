package pcrc.gotbetter.user.data_access.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;

import static pcrc.gotbetter.user.data_access.entity.QSocialAccount.socialAccount;

public class SocialAccountRepositoryImpl implements SocialAccountRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    @Autowired
    public SocialAccountRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsByProviderTypeAndProviderId(ProviderType provider_type, String provider_id) {
        Integer existsUser = queryFactory
                .selectOne()
                .from(socialAccount)
                .where(eqProviderType(provider_type),
                        eqProviderId(provider_id))
                .fetchFirst();
        return existsUser != null;
    }

    private BooleanExpression eqProviderType(ProviderType provider_type) {
        if (StringUtils.isNullOrEmpty(String.valueOf(provider_type))) {
            return null;
        }
        return socialAccount.providerType.eq(provider_type);
    }

    private BooleanExpression eqProviderId(String provider_id) {
        if (StringUtils.isNullOrEmpty(provider_id)) {
            return null;
        }
        return socialAccount.providerId.eq(provider_id);
    }
}
