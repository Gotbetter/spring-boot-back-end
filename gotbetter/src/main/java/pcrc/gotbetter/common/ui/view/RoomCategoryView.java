package pcrc.gotbetter.common.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.common.service.CommonCodeReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomCategoryView {
	private final Integer order;
	private final String room_category_code;
	private final String room_category_description;
	private final String room_category_image;
	private final String updated_date;
	private final String updated_by;

	@Builder
	public RoomCategoryView(CommonCodeReadUseCase.FindCommonCodeResult commonCodeResult) {
		this.order = commonCodeResult.getCommonOrder();
		this.room_category_code = commonCodeResult.getCode();
		this.room_category_description = commonCodeResult.getCodeDescription();
		this.room_category_image = commonCodeResult.getAttribute1();
		this.updated_date = commonCodeResult.getUpdatedDate();
		this.updated_by = commonCodeResult.getUpdatedBy();
	}
}
