package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LimiterConditionStateRequest {
    @NotEmpty
    private List<UUID> ids;

    @NotNull
    private Boolean state;
}
