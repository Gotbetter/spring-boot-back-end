package pcrc.gotbetter.common.data_access.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeId implements Serializable {
    @Column(name = "group_code")
    private String groupCode;
    private String code;

    @Builder
    public CommonCodeId(String groupCode, String code) {
        this.groupCode = groupCode;
        this.code = code;
    }
}
