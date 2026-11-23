import { Pencil, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface ManageRowProps {
  name: string;
  /** Category, plus anything else worth a glance (e.g. the interest rate). */
  detail: string;
  /** Already formatted, sign included — in the user's preferred currency. */
  amount: string;
  /**
   * The row's own native amount, formatted, shown beside `amount` (PRD §4.5.5).
   * Only passed when the row is held in a different currency from the one shown.
   */
  nativeAmount?: string;
  negative?: boolean;
  /** The row currently loaded into the form above. */
  isBeingEdited: boolean;
  onEdit: () => void;
  onDelete: () => void;
}

/** One asset or liability in a manage list: what it is, what it's worth, and its actions. */
export default function ManageRow({
  name,
  detail,
  amount,
  nativeAmount,
  negative = false,
  isBeingEdited,
  onEdit,
  onDelete,
}: ManageRowProps) {
  return (
    <li
      className={cn(
        'flex items-center gap-3 px-4 py-3',
        isBeingEdited && 'bg-teal-50',
      )}
    >
      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-medium text-gray-900">{name}</p>
        <p className="truncate text-xs text-gray-500">{detail}</p>
      </div>
      <span className="text-right tabular-nums">
        <span
          className={cn('text-sm font-medium', negative ? 'text-red-600' : 'text-gray-900')}
        >
          {amount}
        </span>
        {nativeAmount && (
          <span className="block text-xs text-gray-500 sm:ml-1.5 sm:inline">({nativeAmount})</span>
        )}
      </span>
      <div className="flex shrink-0">
        <Button variant="ghost" size="icon" onClick={onEdit} aria-label={`Edit ${name}`}>
          <Pencil />
        </Button>
        <Button
          variant="ghost"
          size="icon"
          onClick={onDelete}
          aria-label={`Delete ${name}`}
          className="text-gray-500 hover:text-red-600"
        >
          <Trash2 />
        </Button>
      </div>
    </li>
  );
}
