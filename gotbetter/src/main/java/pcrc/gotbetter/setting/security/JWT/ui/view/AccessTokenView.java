package pcrc.gotbetter.setting.security.JWT.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenView {
    private final String accessToken;

    @Builder
    public AccessTokenView(String accessToken) {
        this.accessToken = accessToken;
    }
}
