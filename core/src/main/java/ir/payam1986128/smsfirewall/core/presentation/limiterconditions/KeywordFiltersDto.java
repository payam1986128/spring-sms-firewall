package ir.payam1986128.smsfirewall.core.presentation.limiterconditions;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class KeywordFiltersDto {

    private Set<String> keywords;

    private Set<UUID> categories;

    private Set<String> regexes;
}
