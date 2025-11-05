package ir.payam1986128.smsfirewall.core.presentation.sms;

import ir.payam1986128.smsfirewall.core.entity.Action;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class SmsRequest implements Serializable {
    private UUID id;
    private String sender;
    private String receiver;
    private String message;
    private Action action;
}
