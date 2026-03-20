import { Task } from './task';

export interface DailyPriority {
  id: number;
  task: Task;
  rank: number;
  completed: boolean;
  priorityDate: string;
}

export interface DailyPriorityRequest {
  taskIds: number[];
}
