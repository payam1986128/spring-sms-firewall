package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class SenderFiltersDto {
    private Set<String> senders;
    @Valid
    private RateFiltersDto rate;

    public void normalizeSenders(Function<String, String> normalizer) {
        if (senders != null) {
            senders = senders.stream().map(normalizer).collect(Collectors.toSet());
        }
    }
}
