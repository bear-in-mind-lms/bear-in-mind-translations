package com.kwezal.bearinmind.translation.validation;

import static java.util.Objects.isNull;

import com.kwezal.bearinmind.translation.validation.annotation.Locale;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocaleValidator implements ConstraintValidator<Locale, String> {

    private boolean allowEmpty;

    @Override
    public void initialize(final Locale constraintAnnotation) {
        allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (isNull(value) || value.isEmpty()) {
            return allowEmpty;
        }

        final var length = value.length();
        return (
            (length == 2 || length == 4) &&
            (isLowerCaseLatin(value.charAt(0)) && isLowerCaseLatin(value.charAt(1))) &&
            (length == 2 || (isUpperCaseLatin(value.charAt(2)) && isUpperCaseLatin(value.charAt(3))))
        );
    }

    private boolean isLowerCaseLatin(final char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    private boolean isUpperCaseLatin(final char ch) {
        return ch >= 'A' && ch <= 'Z';
    }
}
