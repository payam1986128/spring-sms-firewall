package ir.payam1986128.smsfirewall.core.presentation.sms;

import ir.payam1986128.smsfirewall.core.entity.Action;
import ir.payam1986128.smsfirewall.core.presentation.common.PageableSortable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SmsFilterRequest extends PageableSortable {
    private String sender;
    private String receiver;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo = LocalDateTime.now();
    private Action action;
}
