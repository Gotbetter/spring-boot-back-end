package pcrc.gotbetter.user.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "UserSet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class UserSet {
    @Id
    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "auth_id")
    @NotNull
    private String authId;

    @NotNull
    private String password;

    @Builder
    public UserSet(Long userId, String authId, String password) {
        this.userId = userId;
        this.authId = authId;
        this.password = password;
    }
}
