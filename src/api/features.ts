import { apiGet, apiPatch } from './client';
import { FeatureToggle } from '../types/models';

export const fetchFeatureToggles = () => apiGet<FeatureToggle[]>('/api/features');

export const updateFeatureToggle = (id: string, isEnabled: boolean) =>
  apiPatch<FeatureToggle>(`/api/features/${id}`, { isEnabled });
