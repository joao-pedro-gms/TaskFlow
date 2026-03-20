export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface CategoryInfo {
  id: number;
  name: string;
  colorHex: string;
  iconName: string;
}

export interface Task {
  id: number;
  title: string;
  description?: string;
  deadline?: string;
  status: TaskStatus;
  priority: TaskPriority;
  category?: CategoryInfo;
  reminderSent: boolean;
  createdAt: string;
  completedAt?: string;
}

export interface TaskRequest {
  title: string;
  description?: string;
  deadline?: string;
  priority?: TaskPriority;
  categoryId?: number;
}

export interface Category {
  id: number;
  name: string;
  colorHex: string;
  iconName: string;
}
