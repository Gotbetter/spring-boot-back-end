package pcrc.gotbetter.detail_plan_record.ui.request_body;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class DetailPlanRecordRequest {

	@NotBlank
	private String record_title;

	@NotBlank
	private String record_body;

	@NotNull
	private MultipartFile record_photo;
}
