package ir.payam1986128.smsfirewall.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    String[] fields;

    @Override
    public void initialize(AtLeastOneField constraintAnnotation) {
        fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object == null || Arrays.stream(fields)
                .anyMatch(f -> PARSER.parseExpression(f).getValue(object) != null);
    }
}
