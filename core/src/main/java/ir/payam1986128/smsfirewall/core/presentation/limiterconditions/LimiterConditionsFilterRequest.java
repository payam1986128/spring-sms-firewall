package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import ir.payam1986128.smsfirewall.core.presentation.common.PageableSortable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LimiterConditionsFilterRequest extends PageableSortable {
    private Boolean state;
    private String filter = "";
}
