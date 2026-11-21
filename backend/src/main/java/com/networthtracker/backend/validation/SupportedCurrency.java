package com.networthtracker.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A currency an asset, liability or budget row may be stored in: any code in
 * the latest stored FX set, plus the display currencies so a fresh install with
 * no rates yet still accepts them (PRD §4.5.4). Null is valid — create falls back
 * to the user's preferred currency and update keeps the row's own.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedCurrencyValidator.class)
public @interface SupportedCurrency {

    String message() default "Currency is not supported";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
