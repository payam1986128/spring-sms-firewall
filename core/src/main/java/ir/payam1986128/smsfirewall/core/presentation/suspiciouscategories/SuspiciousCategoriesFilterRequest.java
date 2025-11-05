package ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories;

import ir.payam1986128.smsfirewall.core.presentation.common.PageableSortable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuspiciousCategoriesFilterRequest extends PageableSortable {
    private String name = "";
    private String word;
}
