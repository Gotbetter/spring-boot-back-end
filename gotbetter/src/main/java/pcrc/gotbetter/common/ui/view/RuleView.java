package pcrc.gotbetter.common.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import pcrc.gotbetter.common.service.CommonCodeReadUseCase;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleView {
    private final String rule_code;
    private final String rule_description;
    private final String rule_attribute1;
    private final String rule_attribute2;

    @Builder
    public RuleView(CommonCodeReadUseCase.FindCommonCodeResult commonCodeResult) {
        this.rule_code = commonCodeResult.getCode();
        this.rule_description = commonCodeResult.getCodeDescription();
        this.rule_attribute1 = commonCodeResult.getAttribute1();
        this.rule_attribute2 = commonCodeResult.getAttribute2();
    }
}
