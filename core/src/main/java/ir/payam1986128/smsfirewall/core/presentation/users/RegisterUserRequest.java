package ir.payam1986128.smsfirewall.core.presentation.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Username is required")
    private String username;
}
