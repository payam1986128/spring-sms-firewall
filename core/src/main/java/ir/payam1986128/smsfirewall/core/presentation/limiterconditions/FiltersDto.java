package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import ir.payam1986128.smsfirewall.core.validation.AtLeastOneField;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class FiltersDto {

    @Valid
    @AtLeastOneField(fields = {"keywords", "categories", "regexes"})
    private KeywordFiltersDto keyword;

    @Valid
    @AtLeastOneField(fields = {"rate", "senders"})
    private SenderFiltersDto sender;

    private Set<String> receivers;

    public void normalizeReceivers(Function<String, String> normalizer) {
        if (receivers != null) {
            receivers = receivers.stream().map(normalizer).collect(Collectors.toSet());
        }
    }
}
