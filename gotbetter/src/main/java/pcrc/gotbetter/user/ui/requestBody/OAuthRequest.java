package pcrc.gotbetter.user.ui.requestBody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OAuthRequest {
    @NotNull @NotBlank
    private String id;
    @NotNull @NotBlank @Email
    private String email;
    @NotNull @NotBlank
    private String name;
    @NotNull @NotBlank
    private String picture;
}