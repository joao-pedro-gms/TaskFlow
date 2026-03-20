import { Check, Trash2, Clock, AlertCircle } from 'lucide-react';
import { Task } from '../../types/task';
import { formatDistanceToNow, isPast } from 'date-fns';
import clsx from 'clsx';

interface Props {
  task: Task;
  onComplete: (id: number) => void;
  onDelete: (id: number) => void;
}

const priorityColors = {
  HIGH: 'bg-red-100 text-red-700',
  MEDIUM: 'bg-yellow-100 text-yellow-700',
  LOW: 'bg-green-100 text-green-700',
};

export function TaskCard({ task, onComplete, onDelete }: Props) {
  const isCompleted = task.status === 'COMPLETED';
  const isOverdue = task.deadline && !isCompleted && isPast(new Date(task.deadline));

  return (
    <div
      className={clsx(
        'bg-white rounded-xl border p-4 flex items-start gap-3 group transition-all',
        isCompleted ? 'opacity-60 border-gray-100' : 'border-gray-200 hover:border-primary-200 hover:shadow-sm'
      )}
    >
      {/* Complete button */}
      <button
        onClick={() => !isCompleted && onComplete(task.id)}
        disabled={isCompleted}
        className={clsx(
          'mt-0.5 w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-colors',
          isCompleted
            ? 'bg-green-500 border-green-500'
            : 'border-gray-300 hover:border-primary-500'
        )}
      >
        {isCompleted && <Check size={12} className="text-white" />}
      </button>

      <div className="flex-1 min-w-0">
        <p className={clsx('text-sm font-medium', isCompleted && 'line-through text-gray-400')}>
          {task.title}
        </p>
        {task.description && (
          <p className="text-xs text-gray-400 mt-0.5 truncate">{task.description}</p>
        )}
        <div className="flex items-center gap-2 mt-2 flex-wrap">
          {task.category && (
            <span
              className="text-xs px-2 py-0.5 rounded-full font-medium"
              style={{ backgroundColor: task.category.colorHex + '20', color: task.category.colorHex }}
            >
              {task.category.name}
            </span>
          )}
          <span className={clsx('text-xs px-2 py-0.5 rounded-full font-medium', priorityColors[task.priority])}>
            {task.priority}
          </span>
          {task.deadline && (
            <span className={clsx('flex items-center gap-1 text-xs', isOverdue ? 'text-red-500' : 'text-gray-400')}>
              {isOverdue ? <AlertCircle size={11} /> : <Clock size={11} />}
              {formatDistanceToNow(new Date(task.deadline), { addSuffix: true })}
            </span>
          )}
        </div>
      </div>

      <button
        onClick={() => onDelete(task.id)}
        className="opacity-0 group-hover:opacity-100 p-1.5 text-gray-300 hover:text-red-400 rounded-lg hover:bg-red-50 transition-all"
        title="Delete task"
      >
        <Trash2 size={15} />
      </button>
    </div>
  );
}
