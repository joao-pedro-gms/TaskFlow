import { apiClient } from './client';
import { DailyPriority, DailyPriorityRequest } from '../types/priority';

export const prioritiesApi = {
  getToday: () =>
    apiClient.get<DailyPriority[]>('/priorities/today').then((r) => r.data),

  set: (data: DailyPriorityRequest) =>
    apiClient.post<DailyPriority[]>('/priorities', data).then((r) => r.data),

  complete: (id: number) =>
    apiClient.patch<DailyPriority>(`/priorities/${id}/complete`).then((r) => r.data),
};
