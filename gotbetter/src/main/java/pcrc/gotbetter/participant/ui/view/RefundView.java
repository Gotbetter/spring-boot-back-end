package pcrc.gotbetter.participant.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundView {
	private final Integer refund;

	@Builder
	public RefundView(Integer refund) {
		this.refund = refund;
	}
}
