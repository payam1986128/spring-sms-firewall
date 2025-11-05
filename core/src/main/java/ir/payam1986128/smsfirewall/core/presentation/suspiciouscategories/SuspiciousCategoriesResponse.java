package ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SuspiciousCategoriesResponse {
    private List<SuspiciousCategoryDto> categories;
    private long count;
}
