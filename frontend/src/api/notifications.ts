import { apiClient } from './client';
import { Notification } from '../types/reflection';

export const notificationsApi = {
  getAll: (unreadOnly = false) =>
    apiClient.get<Notification[]>('/notifications', { params: { unreadOnly } }).then((r) => r.data),

  getUnreadCount: () =>
    apiClient.get<{ unread: number }>('/notifications/count').then((r) => r.data.unread),

  markRead: (id: string) =>
    apiClient.patch<Notification>(`/notifications/${id}/read`).then((r) => r.data),

  markAllRead: () => apiClient.patch('/notifications/read-all'),
};
