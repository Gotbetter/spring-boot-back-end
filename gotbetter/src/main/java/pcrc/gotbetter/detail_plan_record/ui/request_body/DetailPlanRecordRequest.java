package pcrc.gotbetter.detail_plan_record.ui.request_body;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class DetailPlanRecordRequest {

	@NotBlank
	private String record_title;

	@NotBlank
	private String record_body;
}
