import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Sun, CheckCircle2, Circle } from 'lucide-react';
import { tasksApi } from '../api/tasks';
import { prioritiesApi } from '../api/priorities';
import { Task } from '../types/task';
import clsx from 'clsx';

export function MorningReview() {
  const qc = useQueryClient();
  const [selected, setSelected] = useState<number[]>([]);
  const [saved, setSaved] = useState(false);

  const { data: tasks = [] } = useQuery({
    queryKey: ['tasks', 'PENDING'],
    queryFn: () => tasksApi.getAll('PENDING'),
  });

  const { data: todayPriorities = [] } = useQuery({
    queryKey: ['priorities', 'today'],
    queryFn: prioritiesApi.getToday,
  });

  const setPriorities = useMutation({
    mutationFn: prioritiesApi.set,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['priorities'] });
      setSaved(true);
    },
  });

  const completePriority = useMutation({
    mutationFn: prioritiesApi.complete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['priorities'] }),
  });

  const hasPriorities = todayPriorities.length > 0;

  const toggleSelect = (taskId: number) => {
    setSelected((prev) =>
      prev.includes(taskId)
        ? prev.filter((id) => id !== taskId)
        : prev.length < 3
        ? [...prev, taskId]
        : prev
    );
  };

  const handleConfirm = () => {
    if (selected.length !== 3) return;
    setPriorities.mutate({ taskIds: selected });
  };

  if (saved || hasPriorities) {
    const priorities = hasPriorities ? todayPriorities : [];
    return (
      <div className="max-w-lg mx-auto space-y-6">
        <div className="text-center">
          <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <Sun className="text-yellow-500" size={24} />
          </div>
          <h1 className="text-xl font-bold text-gray-900">Today's Priorities</h1>
          <p className="text-sm text-gray-500 mt-1">Focus on these 3 tasks today</p>
        </div>

        <div className="space-y-3">
          {(saved ? [] : priorities).concat(hasPriorities ? priorities : []).map((p, idx) => (
            <div
              key={p.id}
              className={clsx(
                'bg-white rounded-xl border p-4 flex items-center gap-3',
                p.completed ? 'opacity-60' : 'border-gray-200'
              )}
            >
              <div className="w-7 h-7 rounded-full bg-primary-50 text-primary-600 text-xs font-bold flex items-center justify-center flex-shrink-0">
                {p.rank}
              </div>
              <div className="flex-1">
                <p className={clsx('text-sm font-medium', p.completed && 'line-through text-gray-400')}>
                  {p.task.title}
                </p>
                {p.task.category && (
                  <span className="text-xs text-gray-400">{p.task.category.name}</span>
                )}
              </div>
              <button
                onClick={() => !p.completed && completePriority.mutate(p.id)}
                disabled={p.completed}
                className="text-gray-300 hover:text-green-500 transition-colors"
              >
                {p.completed ? <CheckCircle2 size={20} className="text-green-400" /> : <Circle size={20} />}
              </button>
            </div>
          ))}
        </div>

        <p className="text-center text-xs text-gray-400">
          Priorities locked for today. Come back tomorrow to set new ones!
        </p>
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto space-y-6">
      <div className="text-center">
        <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <Sun className="text-yellow-500" size={24} />
        </div>
        <h1 className="text-xl font-bold text-gray-900">Morning Review</h1>
        <p className="text-sm text-gray-500 mt-1">
          Select your <strong>3 priorities</strong> for today
        </p>
      </div>

      {/* Progress indicator */}
      <div className="flex items-center justify-center gap-2">
        {[1, 2, 3].map((n) => (
          <div
            key={n}
            className={clsx(
              'w-8 h-8 rounded-full border-2 flex items-center justify-center text-xs font-bold transition-all',
              selected.length >= n
                ? 'border-primary-500 bg-primary-500 text-white'
                : 'border-gray-200 text-gray-300'
            )}
          >
            {n}
          </div>
        ))}
        <span className="text-xs text-gray-400 ml-2">{selected.length}/3 selected</span>
      </div>

      {/* Task list */}
      <div className="space-y-2">
        {tasks.length === 0 ? (
          <div className="text-center py-10 text-gray-400">
            <p className="text-sm">No pending tasks. Add tasks in the Dashboard first!</p>
          </div>
        ) : (
          tasks.map((task: Task) => {
            const isSelected = selected.includes(task.id);
            const rank = selected.indexOf(task.id) + 1;
            return (
              <button
                key={task.id}
                onClick={() => toggleSelect(task.id)}
                className={clsx(
                  'w-full text-left bg-white rounded-xl border p-4 flex items-center gap-3 transition-all',
                  isSelected
                    ? 'border-primary-400 bg-primary-50 ring-1 ring-primary-300'
                    : 'border-gray-200 hover:border-gray-300'
                )}
              >
                <div className={clsx(
                  'w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0',
                  isSelected ? 'bg-primary-500 text-white' : 'bg-gray-100 text-gray-400'
                )}>
                  {isSelected ? rank : ''}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-800 truncate">{task.title}</p>
                  {task.category && (
                    <p className="text-xs text-gray-400">{task.category.name}</p>
                  )}
                </div>
                {isSelected && <CheckCircle2 size={18} className="text-primary-500 flex-shrink-0" />}
              </button>
            );
          })
        )}
      </div>

      <button
        onClick={handleConfirm}
        disabled={selected.length !== 3 || setPriorities.isPending}
        className="w-full py-3 bg-primary-500 text-white rounded-xl font-medium text-sm hover:bg-primary-600 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
      >
        {setPriorities.isPending ? 'Saving...' : 'Confirm my 3 priorities'}
      </button>
    </div>
  );
}
