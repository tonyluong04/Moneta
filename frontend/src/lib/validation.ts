/**
 * Form rules that mirror the backend's bean validation, so the user hears about a
 * bad value before the request rather than from a 400 after it.
 */

/** Mirrors @NotNull + @DecimalMin("0.00") + @Digits(integer = 13, fraction = 2). */
export function validateRequiredAmount(value: string): true | string {
  const trimmed = value.trim();
  if (trimmed === '') return 'Enter an amount';
  if (!/^\d{1,13}(\.\d{1,2})?$/.test(trimmed)) return 'Use a positive number, max 2 decimals';
  return true;
}

/** Mirrors interestRate's @DecimalMin("0.00") + @Digits(integer = 3, fraction = 2). Blank is fine. */
export function validateInterestRate(value: string): true | string {
  const trimmed = value.trim();
  if (trimmed === '') return true;
  if (!/^\d{1,3}(\.\d{1,2})?$/.test(trimmed)) return 'Use 0 to 999.99, max 2 decimals';
  return true;
}

/** Mirrors @NotBlank + @Size(max = 100) on name. */
export function validateName(value: string): true | string {
  const trimmed = value.trim();
  if (trimmed === '') return 'Enter a name';
  if (trimmed.length > 100) return 'Keep it to 100 characters';
  return true;
}
