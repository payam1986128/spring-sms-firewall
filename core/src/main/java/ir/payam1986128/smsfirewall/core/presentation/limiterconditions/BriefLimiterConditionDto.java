package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BriefLimiterConditionDto {
    private String id;
    private String name;
    private LocalDateTime createdTime;
    private int priority;
    private List<TimeLimitDto> timeLimits;
}
