package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class LimiterConditionsResponse {
    private List<BriefLimiterConditionDto> limiterConditions;
    private long count;
    private LocalDateTime searchTime;
}
