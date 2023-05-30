package pcrc.gotbetter.user.data_access.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    private String username;

    @NotNull
    private String email;

    private String profile;

    @Column(name = "role_type", length = 20)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Builder
    public User(Long userId, String username,
                String email, String profile,
                RoleType roleType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profile = profile;
        this.roleType = roleType;
    }
}
