package pcrc.gotbetter.detail_plan.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class DetailPlanCompleteRequest {
    @NotNull
    @NotBlank
    private String approve_comment;
}
