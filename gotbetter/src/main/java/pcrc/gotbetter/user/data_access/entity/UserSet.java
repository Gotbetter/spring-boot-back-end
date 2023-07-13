package pcrc.gotbetter.user.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "UserSet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserSet {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "auth_id", nullable = false)
    private String authId;

    @Column(nullable = false)
    private String password;

    @Builder
    public UserSet(Long userId, String authId, String password) {
        this.userId = userId;
        this.authId = authId;
        this.password = password;
    }
}
