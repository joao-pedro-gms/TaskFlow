import { apiClient } from './client';
import { Task, TaskRequest, Category } from '../types/task';

export const tasksApi = {
  getAll: (status?: string) =>
    apiClient.get<Task[]>('/tasks', { params: status ? { status } : {} }).then((r) => r.data),

  getById: (id: number) =>
    apiClient.get<Task>(`/tasks/${id}`).then((r) => r.data),

  create: (data: TaskRequest) =>
    apiClient.post<Task>('/tasks', data).then((r) => r.data),

  update: (id: number, data: TaskRequest) =>
    apiClient.put<Task>(`/tasks/${id}`, data).then((r) => r.data),

  complete: (id: number) =>
    apiClient.patch<Task>(`/tasks/${id}/complete`).then((r) => r.data),

  delete: (id: number) =>
    apiClient.delete(`/tasks/${id}`),

  getCategories: () =>
    apiClient.get<Category[]>('/categories').then((r) => r.data),

  createCategory: (data: { name: string; colorHex?: string; iconName?: string }) =>
    apiClient.post<Category>('/categories', data).then((r) => r.data),
};
