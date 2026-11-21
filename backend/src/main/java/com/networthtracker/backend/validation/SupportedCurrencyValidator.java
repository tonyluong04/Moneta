package com.networthtracker.backend.validation;

import com.networthtracker.backend.service.CurrencyService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Spring builds this with its dependencies injected, so it can read the stored rates. */
public class SupportedCurrencyValidator implements ConstraintValidator<SupportedCurrency, String> {

    private final CurrencyService currencyService;

    public SupportedCurrencyValidator(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        return code == null || currencyService.isSupportedCurrency(code);
    }
}
