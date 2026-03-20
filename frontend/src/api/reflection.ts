import { apiClient } from './client';
import { ReflectionResponse } from '../types/reflection';

export const reflectionApi = {
  getToday: () =>
    apiClient.get<ReflectionResponse>('/reflection/today').then((r) => r.data),

  getByDate: (date: string) =>
    apiClient.get<ReflectionResponse>('/reflection', { params: { date } }).then((r) => r.data),
};
