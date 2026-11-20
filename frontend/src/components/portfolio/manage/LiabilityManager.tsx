import { useEffect, useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import DeleteConfirmDialog from '@/components/portfolio/manage/DeleteConfirmDialog';
import ManageRow from '@/components/portfolio/manage/ManageRow';
import {
  createLiability,
  deleteLiability,
  fetchLiabilities,
  fetchLiabilityCategories,
  updateLiability,
} from '@/lib/liabilityApi';
import { formatCurrency } from '@/lib/format';
import { validateInterestRate, validateName, validateRequiredAmount } from '@/lib/validation';
import type { Liability, LiabilityCategory } from '@/types/liability';

/** Inputs are strings; they become numbers only when the request is built. */
interface LiabilityFormValues {
  name: string;
  categoryId: string;
  balance: string;
  interestRate: string;
}

const EMPTY_FORM: LiabilityFormValues = { name: '', categoryId: '', balance: '', interestRate: '' };

interface LiabilityManagerProps {
  /** The user's preferred currency — what a new liability will be stored in. */
  currency: string;
  /** Tells the page to refetch the hero and cards. */
  onChanged: () => void;
}

/** "6.24% p.a." for the row detail, or nothing when no rate was given. */
function describeRate(rate: number | null): string {
  return rate === null ? '' : ` · ${rate}% p.a.`;
}

/**
 * The "Manage Liabilities" tab: the same shape as AssetManager, with a balance
 * instead of a value and an optional interest rate. Balances are stored positive
 * and shown negative.
 */
export default function LiabilityManager({ currency, onChanged }: LiabilityManagerProps) {
  const [liabilities, setLiabilities] = useState<Liability[]>([]);
  const [categories, setCategories] = useState<LiabilityCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState<Liability | null>(null);
  const [pendingDelete, setPendingDelete] = useState<Liability | null>(null);
  const [deleting, setDeleting] = useState(false);
  const formRef = useRef<HTMLFormElement>(null);

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<LiabilityFormValues>({ defaultValues: EMPTY_FORM });

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const [loadedLiabilities, loadedCategories] = await Promise.all([
          fetchLiabilities(),
          fetchLiabilityCategories(),
        ]);
        if (!cancelled) {
          setLiabilities(loadedLiabilities);
          setCategories(loadedCategories);
        }
      } catch (error) {
        if (!cancelled) {
          toast.error(error instanceof Error ? error.message : 'Could not load your liabilities');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  /** After a save or delete: the list here, and the hero and cards on the page. */
  async function refresh() {
    try {
      setLiabilities(await fetchLiabilities());
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not reload your liabilities');
    }
    onChanged();
  }

  function startEdit(liability: Liability) {
    setEditing(liability);
    reset({
      name: liability.name,
      categoryId: String(liability.category.id),
      balance: String(liability.balance),
      interestRate: liability.interestRate === null ? '' : String(liability.interestRate),
    });
    formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  function cancelEdit() {
    setEditing(null);
    reset(EMPTY_FORM);
  }

  /**
   * The PUT treats a missing field as "keep it", so blanking a saved rate would
   * silently do nothing. Say so instead; 0 is the way to record no interest.
   */
  function validateRate(value: string): true | string {
    if (editing?.interestRate != null && value.trim() === '') {
      return 'A saved rate can’t be removed; enter 0 for no interest';
    }
    return validateInterestRate(value);
  }

  async function onSubmit(values: LiabilityFormValues) {
    const rate = values.interestRate.trim();
    const body = {
      name: values.name.trim(),
      categoryId: Number(values.categoryId),
      balance: Number(values.balance.trim()),
      ...(rate !== '' && { interestRate: Number(rate) }),
    };
    setSaving(true);
    try {
      if (editing) {
        await updateLiability(editing.id, body);
        toast.success('Liability updated');
      } else {
        await createLiability(body);
        toast.success('Liability added');
      }
      setEditing(null);
      reset(EMPTY_FORM);
      await refresh();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not save the liability');
    } finally {
      setSaving(false);
    }
  }

  async function confirmDelete() {
    if (!pendingDelete) return;
    setDeleting(true);
    try {
      await deleteLiability(pendingDelete.id);
      toast.success(`Deleted “${pendingDelete.name}”`);
      if (editing?.id === pendingDelete.id) cancelEdit();
      setPendingDelete(null);
      await refresh();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not delete the liability');
    } finally {
      setDeleting(false);
    }
  }

  // editing shows the row's own currency, so an old USD row never reads as AUD
  const balanceCurrency = editing ? editing.currency : currency;

  return (
    <div className="space-y-6">
      <form ref={formRef} onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <div className="space-y-2">
            <Label htmlFor="liability-name">Liability Name</Label>
            <Input
              id="liability-name"
              placeholder="E.g., Home Loan, Visa Card"
              aria-invalid={!!errors.name}
              {...register('name', { validate: validateName })}
            />
            {errors.name && <p className="text-xs text-red-600">{errors.name.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="liability-category">Liability Type</Label>
            <Controller
              name="categoryId"
              control={control}
              rules={{ required: 'Choose a liability type' }}
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger id="liability-category" aria-invalid={!!errors.categoryId} onBlur={field.onBlur}>
                    <SelectValue placeholder="Select a liability type" />
                  </SelectTrigger>
                  <SelectContent>
                    {categories.map((category) => (
                      <SelectItem key={category.id} value={String(category.id)}>
                        {category.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
            {errors.categoryId && (
              <p className="text-xs text-red-600">{errors.categoryId.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="liability-balance">Balance ({balanceCurrency})</Label>
            <Input
              id="liability-balance"
              inputMode="decimal"
              placeholder="E.g., 25000.00"
              aria-invalid={!!errors.balance}
              {...register('balance', { validate: validateRequiredAmount })}
            />
            {errors.balance && <p className="text-xs text-red-600">{errors.balance.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="liability-rate">Interest Rate (%)</Label>
            <Input
              id="liability-rate"
              inputMode="decimal"
              placeholder="Optional, e.g. 6.24"
              aria-invalid={!!errors.interestRate}
              {...register('interestRate', { validate: validateRate })}
            />
            {errors.interestRate && (
              <p className="text-xs text-red-600">{errors.interestRate.message}</p>
            )}
          </div>
        </div>

        <div className="flex gap-2">
          <Button type="submit" disabled={saving} className="flex-1">
            {saving ? 'Saving…' : editing ? 'Save changes' : 'Add Liability'}
          </Button>
          {editing && (
            <Button type="button" variant="outline" onClick={cancelEdit} disabled={saving}>
              Cancel
            </Button>
          )}
        </div>
      </form>

      {loading ? (
        <p className="py-6 text-center text-sm text-gray-400">Loading…</p>
      ) : liabilities.length === 0 ? (
        <p className="py-6 text-center text-sm text-gray-500">No liabilities added yet.</p>
      ) : (
        <ul className="divide-y divide-gray-100 rounded-lg border border-gray-200">
          {liabilities.map((liability) => (
            <ManageRow
              key={liability.id}
              name={liability.name}
              detail={liability.category.name + describeRate(liability.interestRate)}
              amount={formatCurrency(-liability.balance, liability.currency)}
              negative={liability.balance > 0}
              isBeingEdited={editing?.id === liability.id}
              onEdit={() => startEdit(liability)}
              onDelete={() => setPendingDelete(liability)}
            />
          ))}
        </ul>
      )}

      <DeleteConfirmDialog
        itemName={pendingDelete?.name ?? null}
        deleting={deleting}
        onCancel={() => setPendingDelete(null)}
        onConfirm={confirmDelete}
      />
    </div>
  );
}
