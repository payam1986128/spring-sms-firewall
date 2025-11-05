package ir.payam1986128.smsfirewall.core.presentation.suspiciouswords;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuspiciousWordDto {
    private String id;
    private String word;
    private LocalDateTime dateTime;
}
