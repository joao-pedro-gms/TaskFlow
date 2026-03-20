import { useState } from 'react';
import { Plus } from 'lucide-react';

interface Props {
  onSubmit: (title: string) => void;
  loading?: boolean;
}

export function QuickCapture({ onSubmit, loading }: Props) {
  const [value, setValue] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = value.trim();
    if (!trimmed) return;
    onSubmit(trimmed);
    setValue('');
  };

  return (
    <form onSubmit={handleSubmit} className="flex gap-2">
      <input
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        placeholder="Add a task... (press Enter)"
        className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary-300 focus:border-transparent bg-white"
      />
      <button
        type="submit"
        disabled={loading || !value.trim()}
        className="px-4 py-2.5 bg-primary-500 text-white rounded-xl text-sm font-medium hover:bg-primary-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center gap-1.5"
      >
        <Plus size={16} />
        Add
      </button>
    </form>
  );
}
