package pcrc.gotbetter.room.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.room.service.RoomReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RankView {
	private final Integer rank_id;
	private final Integer rank;
	private final String username;
	private final String profile;
	private final Integer refund;

	@Builder
	public RankView(RoomReadUseCase.FindRankResult rankResult) {
		this.rank_id = rankResult.getRankId();
		this.rank = rankResult.getRank();
		this.username = rankResult.getUsername();
		this.profile = rankResult.getProfile();
		this.refund = rankResult.getRefund();
	}
}
