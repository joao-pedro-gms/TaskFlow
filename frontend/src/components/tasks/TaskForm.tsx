import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { X } from 'lucide-react';
import { TaskRequest, Category } from '../../types/task';

const schema = z.object({
  title: z.string().min(1, 'Title is required').max(500),
  description: z.string().optional(),
  deadline: z.string().optional(),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH']).optional(),
  categoryId: z.number().optional(),
});

type FormData = z.infer<typeof schema>;

interface Props {
  categories: Category[];
  onSubmit: (data: TaskRequest) => void;
  onClose: () => void;
  loading?: boolean;
}

export function TaskForm({ categories, onSubmit, onClose, loading }: Props) {
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { priority: 'MEDIUM' },
  });

  const onValid = (data: FormData) => {
    onSubmit({
      title: data.title,
      description: data.description,
      deadline: data.deadline || undefined,
      priority: data.priority,
      categoryId: data.categoryId,
    });
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-md shadow-xl">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <h2 className="text-base font-semibold text-gray-900">New Task</h2>
          <button onClick={onClose} className="p-1.5 hover:bg-gray-100 rounded-lg">
            <X size={18} />
          </button>
        </div>
        <form onSubmit={handleSubmit(onValid)} className="p-5 space-y-4">
          <div>
            <label className="text-xs font-medium text-gray-700 mb-1 block">Title *</label>
            <input
              {...register('title')}
              placeholder="What needs to be done?"
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-300"
            />
            {errors.title && <p className="text-xs text-red-500 mt-1">{errors.title.message}</p>}
          </div>

          <div>
            <label className="text-xs font-medium text-gray-700 mb-1 block">Description</label>
            <textarea
              {...register('description')}
              rows={2}
              placeholder="Optional details..."
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-300 resize-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-medium text-gray-700 mb-1 block">Deadline</label>
              <input
                type="datetime-local"
                {...register('deadline')}
                className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-300"
              />
            </div>
            <div>
              <label className="text-xs font-medium text-gray-700 mb-1 block">Priority</label>
              <select
                {...register('priority')}
                className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-300"
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>
          </div>

          <div>
            <label className="text-xs font-medium text-gray-700 mb-1 block">Category</label>
            <select
              {...register('categoryId', { valueAsNumber: true })}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-300"
            >
              <option value="">Auto-categorize</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>

          <div className="flex gap-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 border border-gray-200 rounded-xl text-sm font-medium text-gray-600 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-2.5 bg-primary-500 text-white rounded-xl text-sm font-medium hover:bg-primary-600 disabled:opacity-50"
            >
              {loading ? 'Creating...' : 'Create Task'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
