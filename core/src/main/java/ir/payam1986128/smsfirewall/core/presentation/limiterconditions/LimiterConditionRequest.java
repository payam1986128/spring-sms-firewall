package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import ir.payam1986128.smsfirewall.core.entity.Action;
import ir.payam1986128.smsfirewall.core.validation.AtLeastOneField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LimiterConditionRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer priority;

    private List<@Valid TimeLimitDto> timeLimits;

    @Valid
    @NotNull
    @AtLeastOneField(fields = {"message", "sender", "receiver"})
    private FiltersDto filters;

    @NotNull
    private Action action;
}
