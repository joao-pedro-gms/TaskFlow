import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Dashboard } from './pages/Dashboard';
import { MorningReview } from './pages/MorningReview';
import { NightlyReflection } from './pages/NightlyReflection';
import { AppLayout } from './components/layout/AppLayout';
import { PrivateRoute } from './components/layout/PrivateRoute';

export const router = createBrowserRouter([
  { path: '/login', element: <Login /> },
  { path: '/register', element: <Register /> },
  {
    element: <PrivateRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <Navigate to="/dashboard" replace /> },
          { path: '/dashboard', element: <Dashboard /> },
          { path: '/morning-review', element: <MorningReview /> },
          { path: '/reflect', element: <NightlyReflection /> },
        ],
      },
    ],
  },
  { path: '*', element: <Navigate to="/dashboard" replace /> },
]);
