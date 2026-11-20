import { Card } from '@/components/ui/card';
import PercentRing from '@/components/portfolio/PercentRing';
import { formatCurrency } from '@/lib/format';
import type { AssetCategoryTotal } from '@/types/portfolio';

interface CategoryCardProps {
  category: AssetCategoryTotal;
  totalAssets: number;
  currency: string;
}

/**
 * One asset category: its share of total assets as a ring, then name, subtitle
 * and value. Not a link — Shares and Crypto become links in 5.5 (PRD §4.3.1).
 */
export default function CategoryCard({ category, totalAssets, currency }: CategoryCardProps) {
  // the zero guard sits beside the percentage, per the consistency rules
  const percent = totalAssets > 0 ? (category.total / totalAssets) * 100 : 0;

  return (
    <Card className="flex flex-col items-center gap-3 p-5 text-center border-gray-200 shadow-sm">
      <PercentRing percent={percent} />
      <div className="flex-1">
        <h2 className="text-sm font-semibold text-gray-900">{category.name}</h2>
        {category.description && (
          <p className="mt-1 text-xs text-gray-500">{category.description}</p>
        )}
      </div>
      <p className="text-base font-medium text-gray-900 tabular-nums">
        {formatCurrency(category.total, currency, { wholeUnits: true })}
      </p>
    </Card>
  );
}
