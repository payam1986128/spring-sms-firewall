package ir.payam1986128.smsfirewall.core.presentation.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class SmsResponse {
    private List<BriefSmsDto> sms;
    private long count;
    private LocalDateTime searchTime;
}
