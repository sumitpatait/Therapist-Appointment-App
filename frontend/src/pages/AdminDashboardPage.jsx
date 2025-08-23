// frontend/src/pages/AdminDashboardPage.jsx

import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import './AdminDashboardPage.css';
import './AdminReviewModerationPage.css';

const AdminDashboardPage = () => {
  const { currentUser } = useAuth();

  if (!currentUser || currentUser.role !== 'ADMIN') {
    return <div className="error-message">Access Denied. Only administrators can view this dashboard.</div>;
  }

  return (
    <div className="admin-dashboard-page">
      <h2>Admin Dashboard</h2>
      <p>Welcome, {currentUser.email}!</p>
      <p>Your role: {currentUser.role}</p>

      <h3>System Management</h3>
      <div className="dashboard-links">
        <Link to="/admin/users" className="btn-primary">Manage Users</Link>
        <Link to="/admin/appointments" className="btn-primary">Manage Appointments</Link>
        <Link to="/admin/reviews" className="btn-primary">Moderate Reviews</Link>
        <Link to="/admin/stats" className="btn-primary">View System Statistics</Link> {/* <--- ADD THIS LINE */}
      </div>

      <div className="dashboard-content">
        <p>This section will provide a quick overview of system health and key metrics.</p>
      </div>
    </div>
  );
};

export default AdminDashboardPage;