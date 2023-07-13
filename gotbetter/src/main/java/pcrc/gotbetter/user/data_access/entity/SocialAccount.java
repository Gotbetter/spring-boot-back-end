package pcrc.gotbetter.user.data_access.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import pcrc.gotbetter.setting.BaseTimeEntity;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;

@Entity
@Table(name = "SocialAccount")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class SocialAccount extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id", nullable = false)
    private Long socialAccountId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "provider_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Builder
    public SocialAccount(Long socialAccountId, Long userId,
                         ProviderType providerType, String providerId) {
        this.socialAccountId = socialAccountId;
        this.userId = userId;
        this.providerType = providerType;
        this.providerId = providerId;
    }
}
