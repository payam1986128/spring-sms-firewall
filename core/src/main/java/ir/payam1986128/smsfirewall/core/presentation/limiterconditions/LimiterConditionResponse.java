package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import ir.payam1986128.smsfirewall.core.entity.Action;
import lombok.Data;

import java.util.List;

@Data
public class LimiterConditionResponse {
    private String name;
    private boolean active;
    private int priority;
    private long caughtSms;
    private List<TimeLimitDto> timeLimits;
    private FiltersDto filters;
    private Action action;
}
