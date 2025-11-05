package ir.payam1986128.smsfirewall.core.presentation.suspiciouswords;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class SuspiciousWordsRequest implements Serializable {
    @NotEmpty(message = "Words is required")
    private Set<@NotBlank String> words;
}
