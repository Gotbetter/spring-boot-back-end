package pcrc.gotbetter.user.data_access.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.gotbetter.setting.BaseTimeEntity;

@Entity
@Table(name = "UserSet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class UserSet extends BaseTimeEntity {
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

    public void updateById(String userId) {
        this.updateCreatedById(userId);
        this.updateUpdatedById(userId);
    }
}
