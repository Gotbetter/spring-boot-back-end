package pcrc.gotbetter.user.data_access.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import pcrc.gotbetter.setting.BaseTimeEntity;
import pcrc.gotbetter.user.login_method.login_type.RoleType;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    private String profile;

    @Column(name = "role_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Builder
    public User(Long userId, String username,
                String email, String profile,
                RoleType roleType, String refreshToken,
                String fcmToken) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profile = profile;
        this.roleType = roleType;
        this.refreshToken = refreshToken;
        this.fcmToken = fcmToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateUsername(String username) {
        this.username = username;
    }
}