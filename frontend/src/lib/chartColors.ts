/**
 * Chart series colours — deliberately separate from the seeded category colours.
 *
 * Those are fine as 10px dots beside a text label, but they fail as chart fills:
 * Subscriptions (#8b5cf6) and Insurance (#6366f1) are ΔE 6.3 apart to *normal*
 * vision and 0.8 apart under protanopia, and they sit adjacent in display_order.
 *
 * The values below were checked with the dataviz palette validator against the
 * light chart surface. The budgeted/actual pair passes every gate — lightness
 * band, chroma floor, CVD separation (worst ΔE 24.7 protan) and contrast.
 */
export const SERIES_BUDGETED = '#2a78d6';
export const SERIES_ACTUAL = '#eb6834';

/** The net-money line. A single series, so there is no categorical separation to check. */
export const LINE_NET = '#2a78d6';

/**
 * Donut slices, in fixed assignment order — never cycled.
 *
 * Capped at four because a pie compares every slice against every other, and
 * under that all-pairs test no five-hue set passed CVD separation. Four does,
 * cleanly. Anything past the top four folds into PIE_OTHER.
 */
export const PIE_SLICES = ['#2a78d6', '#eb6834', '#4a3aa7', '#1baf7a'];

/**
 * The "Other" bucket. Deliberately below the validator's chroma floor: that floor
 * keeps categorical identities apart, and "Other" is a residual rather than an
 * identity, so reading as gray is the point.
 */
export const PIE_OTHER = '#9aa0a6';
