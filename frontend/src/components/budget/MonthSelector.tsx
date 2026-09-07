/**Prev/next arrow to navigate months */
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { addMonths, formatMonthLabel } from '@/lib/month';

interface MonthSelectorProps {
  month: Date;
  onChange: (month: Date) => void;
  disabled?: boolean;
}

export default function MonthSelector({ month, onChange, disabled = false }: MonthSelectorProps) {
  return (
    <div className="flex items-center gap-2">
      <Button
        type="button"
        variant="outline"
        size="icon"
        disabled={disabled}
        aria-label="Previous month"
        onClick={() => onChange(addMonths(month, -1))}
        className="rounded-xl border-gray-200"
      >
        <ChevronLeft className="w-4 h-4" />
      </Button>

      <span className="min-w-40 text-center text-sm font-semibold text-gray-900">
        {formatMonthLabel(month)}
      </span>

      <Button
        type="button"
        variant="outline"
        size="icon"
        disabled={disabled}
        aria-label="Next month"
        onClick={() => onChange(addMonths(month, 1))}
        className="rounded-xl border-gray-200"
      >
        <ChevronRight className="w-4 h-4" />
      </Button>
    </div>
  );
}