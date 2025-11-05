package ir.payam1986128.smsfirewall.core.presentation.common;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Sortable implements Serializable {
    private String sort;
}
