package ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class SuspiciousCategoryWordsRequest {

    @NotBlank
    private String name;

    @NotEmpty
    private Set<@NotBlank String> words;
}
