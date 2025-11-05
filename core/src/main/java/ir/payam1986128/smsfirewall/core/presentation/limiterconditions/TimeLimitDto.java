package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeLimitDto {
    private LocalDateTime from;
    @NotNull
    private LocalDateTime to;
}
