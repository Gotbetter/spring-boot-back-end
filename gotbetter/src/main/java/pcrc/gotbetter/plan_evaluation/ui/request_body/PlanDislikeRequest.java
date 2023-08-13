package pcrc.gotbetter.plan_evaluation.ui.request_body;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PlanDislikeRequest {
	@NotNull
	private Long user_id;
}
