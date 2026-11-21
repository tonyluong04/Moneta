package com.networthtracker.backend.validation;

import com.networthtracker.backend.service.CurrencyService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Spring builds this with its dependencies injected, so it can read the configured list. */
public class DisplayCurrencyValidator implements ConstraintValidator<DisplayCurrency, String> {

    private final CurrencyService currencyService;

    public DisplayCurrencyValidator(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        if (code == null || currencyService.isDisplayCurrency(code)) {
            return true;
        }
        // name the choices, e.g. "Currency must be one of AUD, USD"
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Currency must be one of " + String.join(", ", currencyService.displayCurrencies()))
                .addConstraintViolation();
        return false;
    }
}
