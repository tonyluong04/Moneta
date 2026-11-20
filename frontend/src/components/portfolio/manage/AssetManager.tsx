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
  createAsset,
  deleteAsset,
  fetchAssetCategories,
  fetchAssets,
  updateAsset,
} from '@/lib/assetApi';
import { formatCurrency } from '@/lib/format';
import { validateName, validateRequiredAmount } from '@/lib/validation';
import type { Asset, AssetCategory } from '@/types/asset';

/** Inputs are strings; they become numbers only when the request is built. */
interface AssetFormValues {
  name: string;
  categoryId: string;
  value: string;
}

const EMPTY_FORM: AssetFormValues = { name: '', categoryId: '', value: '' };

interface AssetManagerProps {
  /** The user's preferred currency — what a new asset will be stored in. */
  currency: string;
  /** Tells the page to refetch the hero and cards. */
  onChanged: () => void;
}

/**
 * The "Manage Assets" tab: one form for add and edit, then the list.
 * No currency is sent; the backend stores the preferred currency on create and
 * leaves a row's own currency alone on update.
 */
export default function AssetManager({ currency, onChanged }: AssetManagerProps) {
  const [assets, setAssets] = useState<Asset[]>([]);
  const [categories, setCategories] = useState<AssetCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState<Asset | null>(null);
  const [pendingDelete, setPendingDelete] = useState<Asset | null>(null);
  const [deleting, setDeleting] = useState(false);
  const formRef = useRef<HTMLFormElement>(null);

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AssetFormValues>({ defaultValues: EMPTY_FORM });

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const [loadedAssets, loadedCategories] = await Promise.all([
          fetchAssets(),
          fetchAssetCategories(),
        ]);
        if (!cancelled) {
          setAssets(loadedAssets);
          setCategories(loadedCategories);
        }
      } catch (error) {
        if (!cancelled) {
          toast.error(error instanceof Error ? error.message : 'Could not load your assets');
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
      setAssets(await fetchAssets());
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not reload your assets');
    }
    onChanged();
  }

  function startEdit(asset: Asset) {
    setEditing(asset);
    reset({
      name: asset.name,
      categoryId: String(asset.category.id),
      value: String(asset.manualValue),
    });
    formRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  function cancelEdit() {
    setEditing(null);
    reset(EMPTY_FORM);
  }

  async function onSubmit(values: AssetFormValues) {
    const body = {
      name: values.name.trim(),
      categoryId: Number(values.categoryId),
      manualValue: Number(values.value.trim()),
    };
    setSaving(true);
    try {
      if (editing) {
        await updateAsset(editing.id, body);
        toast.success('Asset updated');
      } else {
        await createAsset(body);
        toast.success('Asset added');
      }
      setEditing(null);
      reset(EMPTY_FORM);
      await refresh();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not save the asset');
    } finally {
      setSaving(false);
    }
  }

  async function confirmDelete() {
    if (!pendingDelete) return;
    setDeleting(true);
    try {
      await deleteAsset(pendingDelete.id);
      toast.success(`Deleted “${pendingDelete.name}”`);
      if (editing?.id === pendingDelete.id) cancelEdit();
      setPendingDelete(null);
      await refresh();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Could not delete the asset');
    } finally {
      setDeleting(false);
    }
  }

  // editing shows the row's own currency, so an old USD row never reads as AUD
  const valueCurrency = editing ? editing.currency : currency;

  return (
    <div className="space-y-6">
      <form ref={formRef} onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
        <div className="grid gap-4 md:grid-cols-3">
          <div className="space-y-2">
            <Label htmlFor="asset-name">Asset Name</Label>
            <Input
              id="asset-name"
              placeholder="E.g., Savings Account, Company Stock"
              aria-invalid={!!errors.name}
              {...register('name', { validate: validateName })}
            />
            {errors.name && <p className="text-xs text-red-600">{errors.name.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="asset-category">Asset Type</Label>
            <Controller
              name="categoryId"
              control={control}
              rules={{ required: 'Choose an asset type' }}
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger id="asset-category" aria-invalid={!!errors.categoryId} onBlur={field.onBlur}>
                    <SelectValue placeholder="Select an asset type" />
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
            <Label htmlFor="asset-value">Value ({valueCurrency})</Label>
            <Input
              id="asset-value"
              inputMode="decimal"
              placeholder="E.g., 5000.00"
              aria-invalid={!!errors.value}
              {...register('value', { validate: validateRequiredAmount })}
            />
            {errors.value && <p className="text-xs text-red-600">{errors.value.message}</p>}
          </div>
        </div>

        <div className="flex gap-2">
          <Button type="submit" disabled={saving} className="flex-1">
            {saving ? 'Saving…' : editing ? 'Save changes' : 'Add Asset'}
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
      ) : assets.length === 0 ? (
        <p className="py-6 text-center text-sm text-gray-500">No assets added yet.</p>
      ) : (
        <ul className="divide-y divide-gray-100 rounded-lg border border-gray-200">
          {assets.map((asset) => (
            <ManageRow
              key={asset.id}
              name={asset.name}
              detail={asset.category.name}
              amount={formatCurrency(asset.value, asset.currency)}
              isBeingEdited={editing?.id === asset.id}
              onEdit={() => startEdit(asset)}
              onDelete={() => setPendingDelete(asset)}
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
