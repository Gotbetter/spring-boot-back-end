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
    private final String username;
    private final Integer rank;
    private final Integer refund;

    @Builder
    public RankView(RoomReadUseCase.FindRankResult rankResult) {
        this.username = rankResult.getUsername();
        this.rank = rankResult.getRank();
        this.refund = rankResult.getRefund();
    }
}
