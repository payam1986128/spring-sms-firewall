package ir.payam1986128.smsfirewall.core.presentation.suspiciouswords;

import ir.payam1986128.smsfirewall.core.presentation.common.PageableSortable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuspiciousWordsFilterRequest extends PageableSortable {
    private String filter = "";
}
