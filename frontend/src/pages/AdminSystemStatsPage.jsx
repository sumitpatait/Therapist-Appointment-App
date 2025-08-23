// frontend/src/pages/AdminSystemStatsPage.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import AdminService from '../services/admin.service.js'; // Import Admin Service
import { useNavigate } from 'react-router-dom';
import './AdminSystemStatsPage.css';


const AdminSystemStatsPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  // Function to fetch system statistics
  const fetchStats = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Authentication or Admin role required to view statistics.');
      setLoading(false);
      return;
    }
    try {
      const data = await AdminService.getSystemStatistics(); // Calls backend GET /api/admin/stats
      setStats(data);
    } catch (err) {
      console.error('Failed to fetch system statistics:', err);
      setError(err.response?.data || 'Failed to load statistics. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [currentUser]); // Depend on currentUser for re-fetching

  useEffect(() => {
    fetchStats(); // Initial fetch when component mounts
  }, [fetchStats]); // fetchStats is wrapped in useCallback

  if (loading) {
    return <div>Loading system statistics...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (!stats) {
    return <div>No statistics available.</div>;
  }

  return (
    <div className="admin-stats-page">
      <h2>System Statistics Overview</h2>
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Users</h3>
          <p>{stats.totalUsers}</p>
        </div>
        <div className="stat-card">
          <h3>Patients</h3>
          <p>{stats.totalPatients}</p>
        </div>
        <div className="stat-card">
          <h3>Therapists</h3>
          <p>{stats.totalTherapists}</p>
        </div>
        <div className="stat-card">
          <h3>Total Appointments</h3>
          <p>{stats.totalAppointments}</p>
        </div>
        <div className="stat-card">
          <h3>Pending Appointments</h3>
          <p>{stats.pendingAppointments}</p>
        </div>
        <div className="stat-card">
          <h3>Confirmed Appointments</h3>
          <p>{stats.confirmedAppointments}</p>
        </div>
        <div className="stat-card">
          <h3>Completed Appointments</h3>
          <p>{stats.completedAppointments}</p>
        </div>
        <div className="stat-card">
          <h3>Total Reviews</h3>
          <p>{stats.totalReviews}</p>
        </div>
      </div>
      <button onClick={() => navigate('/admin/dashboard')} className="btn-secondary">Back to Dashboard</button>
    </div>
  );
};

export default AdminSystemStatsPage;