package pcrc.gotbetter.user.ui.requestBody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class UserJoinRequest {

    @NotNull @NotBlank
    private String auth_id;
    @NotNull @NotBlank
    private String password;
    @NotNull @NotBlank
    private String username;
    @NotNull @NotBlank @Email
    private String email;

}