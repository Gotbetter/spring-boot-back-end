package pcrc.gotbetter.common.ui.request_body;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CommonUpdateRequest {
	@NotBlank
	private String group_code;
	@NotBlank
	private String code;
	@NotBlank
	private String code_description;
	private String attribute1;
	private String attribute2;
}
