// frontend/src/App.jsx

import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import './index.css';

// Import your page components
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import TherapistListPage from './pages/TherapistListPage';
import TherapistDetailPage from './pages/TherapistDetailPage';
import PatientAppointmentsPage from './pages/PatientAppointmentsPage';
import TherapistDashboardPage from './pages/TherapistDashboardPage';
import TherapistScheduleManagerPage from './pages/TherapistScheduleManagerPage';
import TherapistAppointmentsManagerPage from './pages/TherapistAppointmentsManagerPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import AdminUserManagementPage from './pages/AdminUserManagementPage';
import AdminUserDetailPage from './pages/AdminUserDetailPage';
import AdminAppointmentManagementPage from './pages/AdminAppointmentManagementPage';
import AdminReviewModerationPage from './pages/AdminReviewModerationPage';
import AdminSystemStatsPage from './pages/AdminSystemStatsPage'; // <--- IMPORT NEW ADMIN STATS MANAGER

// Import Navbar
import Navbar from './components/Navbar';

// PrivateRoute component (remains the same)
const PrivateRoute = ({ children, allowedRoles }) => {
  const { currentUser, isLoading } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!currentUser || !currentUser.token) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(currentUser.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};


function App() {
  return (
    <Router>
      <Navbar />
      <div className="container">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/unauthorized" element={<h2>Access Denied! You do not have permission to view this page.</h2>} />

          {/* Protected Routes */}
          <Route 
            path="/dashboard" 
            element={
              <PrivateRoute allowedRoles={['PATIENT', 'THERAPIST', 'ADMIN']}>
                <h2>Welcome to your Dashboard!</h2>
              </PrivateRoute>
            } 
          />
          {/* Patient Routes */}
          <Route 
            path="/therapists" 
            element={
              <PrivateRoute allowedRoles={['PATIENT']}>
                <TherapistListPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/therapists/:therapistId" 
            element={
              <PrivateRoute allowedRoles={['PATIENT']}>
                <TherapistDetailPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/my-appointments" 
            element={
              <PrivateRoute allowedRoles={['PATIENT']}>
                <PatientAppointmentsPage />
              </PrivateRoute>
            } 
          />
          {/* Therapist Routes */}
          <Route 
            path="/therapist/dashboard" 
            element={
              <PrivateRoute allowedRoles={['THERAPIST']}>
                <TherapistDashboardPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/therapist/schedule" 
            element={
              <PrivateRoute allowedRoles={['THERAPIST']}>
                <TherapistScheduleManagerPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/therapist/appointments" 
            element={
              <PrivateRoute allowedRoles={['THERAPIST']}>
                <TherapistAppointmentsManagerPage />
              </PrivateRoute>
            } 
          />
          {/* Admin Routes */}
          <Route 
            path="/admin/dashboard" 
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminDashboardPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/admin/users" 
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminUserManagementPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/admin/users/:userId" 
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminUserDetailPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/admin/appointments" 
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminAppointmentManagementPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/admin/reviews" 
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminReviewModerationPage />
              </PrivateRoute>
            } 
          />
          {/* <--- ADD NEW ADMIN STATS MANAGER ROUTE --- */}
          <Route 
            path="/admin/stats" 
            element={
              <PrivateRoute allowedRoles={['ADMIN']}>
                <AdminSystemStatsPage />
              </PrivateRoute>
            } 
          />
          {/* --- END NEW ADMIN STATS MANAGER ROUTE --- */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;