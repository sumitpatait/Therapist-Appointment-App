// frontend/src/pages/TherapistDashboardPage.jsx

import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import './TherapistDashboardPage.css';

const TherapistDashboardPage = () => {
  const { currentUser } = useAuth();

  if (!currentUser || currentUser.role !== 'THERAPIST') {
    return <div>Access Denied. Only therapists can view this dashboard.</div>;
  }

  return (
    <div className="therapist-dashboard-page">
      <h2>Therapist Dashboard</h2>
      <p>Welcome, {currentUser.email}!</p>
      <p>{currentUser.role}</p>

      <h3>Your Practice Management</h3>
      <div className="dashboard-links">
        {/* Link to schedule management (will create next) */}
        <Link to="/therapist/schedule" className="btn-primary">Manage My Schedule</Link>
        {/* Link to view appointments (will create next) */}
        <Link to="/therapist/appointments" className="btn-primary">View My Appointments</Link>
        {/* Link to profile management (future) */}
        {/* <Link to="/therapist/profile" className="btn-primary">Edit My Profile</Link> */}
      </div>

      {/* Content for managing schedule/appointments will be displayed via nested routes or components */}
    </div>
  );
};

export default TherapistDashboardPage;