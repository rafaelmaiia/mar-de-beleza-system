import { Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { ProtectedRoute } from './components/ProtectedRoute';
import { ServiceManagementPage } from './pages/ServiceManagementPage';
import { UserManagementPage } from './pages/UserManagementPage';
import { ClientManagementPage } from './pages/ClientManagementPage';
import { SettingsPage } from './pages/SettingsPage';
import { AppointmentManagementPage } from './pages/AppointmentManagementPage';

function App() {
  return (
    <>
      <Toaster 
        position="top-right"
        toastOptions={{
          success: {
            style: {
              background: '#2ecc71',
              color: 'white',
            },
          },
          error: {
            style: {
              background: '#e74c3c',
              color: 'white',
            },
          },
        }}
      />

      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<LoginPage />} />
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/appointments" 
          element={
            <ProtectedRoute>
              <AppointmentManagementPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/services"
          element={
            <ProtectedRoute>
              <ServiceManagementPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/users"
          element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <UserManagementPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/clients"
          element={
            <ProtectedRoute>
              <ClientManagementPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/settings"
          element={
            <ProtectedRoute>
              <SettingsPage />
            </ProtectedRoute>
          } 
        />
      </Routes>
    </>
  );
}

export default App;