import { apiFetch } from '@/lib/api';
import type {
  Asset,
  AssetCategory,
  CreateAssetRequest,
  UpdateAssetRequest,
} from '@/types/asset';

/** GET /api/v1/assets/categories — the 8 seeded categories, in card order. */
export function fetchAssetCategories(): Promise<AssetCategory[]> {
  return apiFetch<AssetCategory[]>('/api/v1/assets/categories');
}

/** GET /api/v1/assets — every asset the user owns, ordered by category then name. */
export function fetchAssets(): Promise<Asset[]> {
  return apiFetch<Asset[]>('/api/v1/assets');
}

/** POST /api/v1/assets — the backend fills in the user's preferred currency. */
export function createAsset(body: CreateAssetRequest): Promise<Asset> {
  return apiFetch<Asset>('/api/v1/assets', {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

/** PUT /api/v1/assets/{id} — partial update; only the fields sent are applied. */
export function updateAsset(id: number, body: UpdateAssetRequest): Promise<Asset> {
  return apiFetch<Asset>(`/api/v1/assets/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  });
}

/** DELETE /api/v1/assets/{id} — 204, no body. */
export function deleteAsset(id: number): Promise<void> {
  return apiFetch<void>(`/api/v1/assets/${id}`, { method: 'DELETE' });
}
