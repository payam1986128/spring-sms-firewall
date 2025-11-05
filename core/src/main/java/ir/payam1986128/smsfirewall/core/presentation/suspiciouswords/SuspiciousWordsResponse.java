package ir.payam1986128.smsfirewall.core.presentation.suspiciouswords;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SuspiciousWordsResponse {
    private List<SuspiciousWordDto> words;
    private long count;
}
