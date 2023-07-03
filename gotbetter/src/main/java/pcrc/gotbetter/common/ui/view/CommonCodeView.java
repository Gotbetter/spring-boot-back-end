package pcrc.gotbetter.common.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.common.service.CommonCodeReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonCodeView {
    private final String code;
    private final String code_description;
    private final String attribute1;
    private final String attribute2;
    private final String attribute3;

    @Builder
    public CommonCodeView(CommonCodeReadUseCase.FindCommonCodeResult commonCodeResult) {
        this.code = commonCodeResult.getCode();
        this.code_description = commonCodeResult.getCodeDescription();
        this.attribute1 = commonCodeResult.getAttribute1();
        this.attribute2 = commonCodeResult.getAttribute2();
        this.attribute3 = commonCodeResult.getAttribute3();
    }
}
