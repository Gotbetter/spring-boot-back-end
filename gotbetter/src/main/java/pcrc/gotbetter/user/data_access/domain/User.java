package pcrc.gotbetter.user.data_access.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "auth_id")
    private String authId;
    private String password;
    private String username;
    private String email;
    private String profile;
    private String refresh_token;

    @Builder
    public User(String authId, String password, String username, String email, String profile) {
        this.authId = authId;
        this.password = password;
        this.username = username;
        this.email = email;
        this.profile = profile;
    }
}
