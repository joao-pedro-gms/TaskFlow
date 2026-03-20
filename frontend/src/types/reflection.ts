import { Task } from './task';
import { DailyPriority } from './priority';

export interface ReflectionResponse {
  date: string;
  completedTasks: Task[];
  totalCompleted: number;
  priorities: DailyPriority[];
  prioritiesCompleted: number;
  prioritiesTotal: number;
}

export interface Notification {
  id: string;
  userId: number;
  message: string;
  type: string;
  read: boolean;
  createdAt: string;
  readAt?: string;
  payload?: Record<string, unknown>;
}
