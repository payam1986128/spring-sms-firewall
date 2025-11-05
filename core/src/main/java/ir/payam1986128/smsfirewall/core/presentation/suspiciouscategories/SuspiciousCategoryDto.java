package ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class SuspiciousCategoryDto {
    private String id;
    private String name;
    private LocalDateTime dateTime;
    private Set<String> words;
}
