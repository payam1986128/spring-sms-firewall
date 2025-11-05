package ir.payam1986128.smsfirewall.core.presentation.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class PageableSortable extends Sortable {
    private int page = 1;
    private int pageSize = 10;
}
