package pcrc.gotbetter.plan.ui.requestBody;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PlanCreateRequest {
    @NotNull
    private Long participant_id;
}
