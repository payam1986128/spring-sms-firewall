package ir.payam1986128.smsfirewall.core.presentation.sms;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SmsTime {
    private UUID id;
    private LocalDateTime receivedTime;
}
