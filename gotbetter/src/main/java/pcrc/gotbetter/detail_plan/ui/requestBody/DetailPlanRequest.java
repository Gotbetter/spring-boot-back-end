package pcrc.gotbetter.detail_plan.ui.requestBody;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class DetailPlanRequest {
	@NotBlank
	private String content;
}
