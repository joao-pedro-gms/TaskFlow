import { useNavigate } from 'react-router-dom';
import { LogOut } from 'lucide-react';
import { useAuthStore } from '../../store/authStore';
import { NotificationBell } from '../notifications/NotificationBell';

export function TopBar() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6">
      <p className="text-sm text-gray-500">
        {new Date().toLocaleDateString('pt-BR', {
          weekday: 'long',
          day: 'numeric',
          month: 'long',
        })}
      </p>
      <div className="flex items-center gap-3">
        <NotificationBell />
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-full bg-primary-500 flex items-center justify-center text-white text-sm font-semibold">
            {user?.displayName?.[0]?.toUpperCase() ?? 'U'}
          </div>
          <span className="text-sm font-medium text-gray-700 hidden sm:block">
            {user?.displayName}
          </span>
        </div>
        <button
          onClick={handleLogout}
          className="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100 transition-colors"
          title="Logout"
        >
          <LogOut size={18} />
        </button>
      </div>
    </header>
  );
}
