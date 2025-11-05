package ir.payam1986128.smsfirewall.core.presentation.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class ValidationErrorDto {
    private String message;
    private Map<String, String> params;
}
