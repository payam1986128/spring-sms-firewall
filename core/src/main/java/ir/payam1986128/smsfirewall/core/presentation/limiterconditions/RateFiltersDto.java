package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RateFiltersDto {

    @NotNull
    @Min(1)
    @Max(10)
    private Integer intervalMinutes;

    @NotNull
    @Min(5)
    private Long threshold;
}
