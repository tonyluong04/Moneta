package com.networthtracker.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A currency a user may pick as their preferred one: on the configured display
 * list, moneta.currencies.display (PRD §4.5.4). Null is valid — the field is
 * optional, and leaving it out keeps the current preference.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DisplayCurrencyValidator.class)
public @interface DisplayCurrency {

    // replaced in the validator by a message naming the allowed currencies
    String message() default "Currency is not one of the display currencies";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
