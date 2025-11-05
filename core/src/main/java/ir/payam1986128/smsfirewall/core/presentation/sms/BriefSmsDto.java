package ir.payam1986128.smsfirewall.core.presentation.sms;

import ir.payam1986128.smsfirewall.core.entity.Action;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BriefSmsDto {
    private String id;
    private String sender;
    private String receiver;
    private String message;
    private UUID appliedFilterId;
    private LocalDateTime receivedTime;
    private LocalDateTime evaluatedTime;
    private LocalDateTime sendTime;
    private Action action;
}
