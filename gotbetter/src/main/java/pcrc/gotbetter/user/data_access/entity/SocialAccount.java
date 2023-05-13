package pcrc.gotbetter.user.data_access.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import pcrc.gotbetter.user.login_method.login_type.ProviderType;

@Entity
@Table(name = "SocialAccount")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_account_id")
    private Long socialAccountId;

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "provider_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Column(name = "provider_id")
    @NotNull
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
