import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, ListTodo, CheckCircle2, Clock } from 'lucide-react';
import { tasksApi } from '../api/tasks';
import { QuickCapture } from '../components/tasks/QuickCapture';
import { TaskCard } from '../components/tasks/TaskCard';
import { TaskForm } from '../components/tasks/TaskForm';
import { useAuthStore } from '../store/authStore';

export function Dashboard() {
  const user = useAuthStore((s) => s.user);
  const qc = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [filter, setFilter] = useState<'ALL' | 'PENDING' | 'COMPLETED'>('ALL');

  const { data: tasks = [] } = useQuery({
    queryKey: ['tasks', filter],
    queryFn: () => tasksApi.getAll(filter === 'ALL' ? undefined : filter),
  });

  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: tasksApi.getCategories,
  });

  const createTask = useMutation({
    mutationFn: tasksApi.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks'] }),
  });

  const completeTask = useMutation({
    mutationFn: tasksApi.complete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks'] }),
  });

  const deleteTask = useMutation({
    mutationFn: tasksApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks'] }),
  });

  const totalTasks = tasks.length;
  const completedToday = tasks.filter((t) => t.status === 'COMPLETED').length;
  const pendingCount = tasks.filter((t) => t.status === 'PENDING').length;

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-xl font-bold text-gray-900">
          Good {getTimeOfDay()}, {user?.displayName?.split(' ')[0]}!
        </h1>
        <p className="text-sm text-gray-500 mt-0.5">Here's your task overview</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-3">
        {[
          { icon: ListTodo, label: 'Total', value: totalTasks, color: 'text-primary-500' },
          { icon: Clock, label: 'Pending', value: pendingCount, color: 'text-yellow-500' },
          { icon: CheckCircle2, label: 'Done', value: completedToday, color: 'text-green-500' },
        ].map(({ icon: Icon, label, value, color }) => (
          <div key={label} className="bg-white rounded-xl border border-gray-200 p-4 text-center">
            <Icon className={`mx-auto mb-1 ${color}`} size={20} />
            <p className="text-xl font-bold text-gray-900">{value}</p>
            <p className="text-xs text-gray-400">{label}</p>
          </div>
        ))}
      </div>

      {/* Quick Capture */}
      <QuickCapture
        onSubmit={(title) => createTask.mutate({ title })}
        loading={createTask.isPending}
      />

      {/* Filters + Add button */}
      <div className="flex items-center justify-between">
        <div className="flex gap-1 bg-gray-100 p-1 rounded-lg">
          {(['ALL', 'PENDING', 'COMPLETED'] as const).map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-3 py-1.5 rounded-md text-xs font-medium transition-colors ${
                filter === f ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {f === 'ALL' ? 'All' : f === 'PENDING' ? 'Pending' : 'Done'}
            </button>
          ))}
        </div>
        <button
          onClick={() => setShowForm(true)}
          className="flex items-center gap-1.5 px-3 py-1.5 bg-primary-500 text-white rounded-lg text-xs font-medium hover:bg-primary-600 transition-colors"
        >
          <Plus size={14} /> New task
        </button>
      </div>

      {/* Task List */}
      <div className="space-y-2">
        {tasks.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            <ListTodo className="mx-auto mb-3" size={36} />
            <p className="text-sm">No tasks yet. Add one above!</p>
          </div>
        ) : (
          tasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              onComplete={(id) => completeTask.mutate(id)}
              onDelete={(id) => deleteTask.mutate(id)}
            />
          ))
        )}
      </div>

      {/* Task Form Modal */}
      {showForm && (
        <TaskForm
          categories={categories}
          onSubmit={(data) => {
            createTask.mutate(data, { onSuccess: () => setShowForm(false) });
          }}
          onClose={() => setShowForm(false)}
          loading={createTask.isPending}
        />
      )}
    </div>
  );
}

function getTimeOfDay() {
  const h = new Date().getHours();
  if (h < 12) return 'morning';
  if (h < 18) return 'afternoon';
  return 'evening';
}
