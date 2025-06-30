import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Stack } from '@mantine/core';
import { HomePage } from './pages/Home.page';
import { DashboardPage } from './pages/Dashboard.page';
import { PrivateRoute } from './PrivateRoute';
import { Welcome } from './components/Welcome/Welcome';
import { Hero } from './components/DashboardHome/Hero';
import { Top10 } from './components/DashboardHome/Top10';
import { DashboardHome } from './pages/DashboardHome.page';
import { DashboardDiscover } from './pages/DashboardDiscover.page';
import { DashboardRecommendation } from './pages/DashboardRecommendation.page';
import { DashboardPlaylists } from './pages/DashboardPlaylists.page';
import { DashboardProfile } from './pages/DashboardProfile.page';

const router = createBrowserRouter([
  {
    path: '/',
    element: <HomePage />,
  },
  {
    path: '/dashboard',
    element: (
      <PrivateRoute>
        <DashboardPage />
      </PrivateRoute>
    ),
    children: [
      {
        path: '', 
        element: <DashboardHome />, 
      },
      {
        path: 'discover', 
        element: <DashboardDiscover />, 
      },
      {
        path: 'recommendations',
        element: <DashboardRecommendation />,
      },
      {
        path: 'playlists',
        element: <DashboardPlaylists />,
      },
      {
        path: 'profile',
        element: <DashboardProfile />,
      }
      
    ],
  },
]);

export function Router() {
  return(
    <RouterProvider router={router} />
  ) 
}
