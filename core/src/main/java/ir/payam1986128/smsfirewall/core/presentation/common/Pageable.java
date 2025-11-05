package ir.payam1986128.smsfirewall.core.presentation.common;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Pageable implements Serializable {
    private int page;
    private int pageSize;
}
