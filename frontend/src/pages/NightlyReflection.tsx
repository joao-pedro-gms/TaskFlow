import { useQuery } from '@tanstack/react-query';
import { Moon, CheckCircle2, Target, TrendingUp } from 'lucide-react';
import { reflectionApi } from '../api/reflection';
import { format } from 'date-fns';
import clsx from 'clsx';

const moods = ['😩', '😕', '😐', '🙂', '😄'];

export function NightlyReflection() {
  const { data: reflection, isLoading } = useQuery({
    queryKey: ['reflection', 'today'],
    queryFn: reflectionApi.getToday,
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-primary-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  const completionRate = reflection?.prioritiesTotal
    ? Math.round((reflection.prioritiesCompleted / reflection.prioritiesTotal) * 100)
    : 0;

  return (
    <div className="max-w-lg mx-auto space-y-6">
      {/* Header */}
      <div className="text-center">
        <div className="w-12 h-12 bg-indigo-100 rounded-full flex items-center justify-center mx-auto mb-3">
          <Moon className="text-indigo-500" size={24} />
        </div>
        <h1 className="text-xl font-bold text-gray-900">Nightly Reflection</h1>
        <p className="text-sm text-gray-500 mt-1">
          {format(new Date(), "EEEE, MMMM do")}
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-3">
        <StatCard
          icon={<CheckCircle2 className="text-green-500" size={20} />}
          value={reflection?.totalCompleted ?? 0}
          label="Tasks done"
          bg="bg-green-50"
        />
        <StatCard
          icon={<Target className="text-primary-500" size={20} />}
          value={`${reflection?.prioritiesCompleted ?? 0}/${reflection?.prioritiesTotal ?? 0}`}
          label="Priorities"
          bg="bg-primary-50"
        />
        <StatCard
          icon={<TrendingUp className="text-orange-500" size={20} />}
          value={`${completionRate}%`}
          label="Completion"
          bg="bg-orange-50"
        />
      </div>

      {/* Priority review */}
      {(reflection?.priorities ?? []).length > 0 && (
        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <h2 className="text-sm font-semibold text-gray-900 mb-3">Priority Goals</h2>
          <div className="space-y-2">
            {reflection!.priorities.map((p) => (
              <div key={p.id} className="flex items-center gap-3">
                <div className={clsx(
                  'w-5 h-5 rounded-full flex items-center justify-center flex-shrink-0',
                  p.completed ? 'bg-green-100' : 'bg-gray-100'
                )}>
                  {p.completed
                    ? <CheckCircle2 size={14} className="text-green-500" />
                    : <span className="text-xs text-gray-400">{p.rank}</span>
                  }
                </div>
                <span className={clsx(
                  'text-sm flex-1',
                  p.completed ? 'line-through text-gray-400' : 'text-gray-700'
                )}>
                  {p.task.title}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Completed tasks */}
      {(reflection?.completedTasks ?? []).length > 0 && (
        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <h2 className="text-sm font-semibold text-gray-900 mb-3">
            Completed Today ({reflection?.totalCompleted})
          </h2>
          <div className="space-y-2">
            {reflection!.completedTasks.map((task) => (
              <div key={task.id} className="flex items-center gap-2">
                <CheckCircle2 size={15} className="text-green-400 flex-shrink-0" />
                <span className="text-sm text-gray-600 line-through">{task.title}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Mood */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <h2 className="text-sm font-semibold text-gray-900 mb-3">How was your day?</h2>
        <div className="flex justify-around">
          {moods.map((emoji, i) => (
            <button
              key={i}
              className="text-2xl hover:scale-125 transition-transform p-1 rounded-lg hover:bg-gray-50"
              title={['Terrible', 'Bad', 'Okay', 'Good', 'Great'][i]}
            >
              {emoji}
            </button>
          ))}
        </div>
      </div>

      {(reflection?.completedTasks ?? []).length === 0 && (reflection?.priorities ?? []).length === 0 && (
        <div className="text-center py-8 text-gray-400">
          <Moon className="mx-auto mb-3 text-indigo-200" size={36} />
          <p className="text-sm">No activity recorded today yet.</p>
          <p className="text-xs mt-1">Complete tasks and set morning priorities to see your progress here!</p>
        </div>
      )}
    </div>
  );
}

function StatCard({ icon, value, label, bg }: {
  icon: React.ReactNode;
  value: string | number;
  label: string;
  bg: string;
}) {
  return (
    <div className={`${bg} rounded-xl p-4 text-center`}>
      <div className="flex justify-center mb-1">{icon}</div>
      <p className="text-xl font-bold text-gray-900">{value}</p>
      <p className="text-xs text-gray-500">{label}</p>
    </div>
  );
}
