package ir.payam1986128.smsfirewall.core.presentation.sms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SmsStateRequest {
    @NotEmpty
    private List<@NotBlank UUID> ids;
    private boolean accepted;
}
