import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Sun, Moon, CheckSquare } from 'lucide-react';
import clsx from 'clsx';

const links = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/morning-review', icon: Sun, label: 'Morning Review' },
  { to: '/reflect', icon: Moon, label: 'Nightly Reflect' },
];

export function Sidebar() {
  return (
    <aside className="w-56 bg-white border-r border-gray-200 flex flex-col">
      <div className="flex items-center gap-2 px-5 py-5 border-b border-gray-100">
        <CheckSquare className="text-primary-500" size={22} />
        <span className="font-bold text-lg text-gray-900">TaskFlow</span>
      </div>
      <nav className="flex-1 p-3 space-y-1">
        {links.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              clsx(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary-50 text-primary-600'
                  : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
              )
            }
          >
            <Icon size={18} />
            {label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
