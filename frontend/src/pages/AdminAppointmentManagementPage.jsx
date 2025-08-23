// frontend/src/pages/AdminAppointmentManagementPage.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import AdminService from '../services/admin.service.js'; // Import Admin Service
import { useNavigate } from 'react-router-dom';
import './AdminAppointmentManagementPage.css';


const AdminAppointmentManagementPage = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(''); // For success/error messages
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  // Function to fetch all appointments
  const fetchAppointments = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Authentication or Admin role required to view appointments.');
      setLoading(false);
      return;
    }
    try {
      const data = await AdminService.getAllAppointments(); // Calls backend GET /api/admin/appointments
      setAppointments(data);
    } catch (err) {
      console.error('Failed to fetch appointments:', err);
      setError(err.response?.data || 'Failed to load appointments. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [currentUser]); // Depend on currentUser for re-fetching

  useEffect(() => {
    fetchAppointments(); // Initial fetch when component mounts
  }, [fetchAppointments]); // fetchAppointments is wrapped in useCallback

  // Handle status update
  const handleStatusUpdate = async (appointmentId, newStatus) => {
    setMessage('');
    setError(null);
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Unauthorized action. Please log in as an admin.');
      return;
    }
    if (window.confirm(`Are you sure you want to change status of appointment ${appointmentId} to ${newStatus}?`)) {
      try {
        await AdminService.updateAppointmentStatus(appointmentId, newStatus); // Calls backend PUT /api/admin/appointments/{id}/status
        setMessage(`Appointment ${appointmentId} updated to ${newStatus} successfully!`);
        fetchAppointments(); // Refresh the list
      } catch (err) {
        console.error(`Failed to update appointment ${appointmentId} status:`, err);
        setError(err.response?.data || 'Failed to update appointment status. Please try again.');
      }
    }
  };

  if (loading) {
    return <div>Loading appointments...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (appointments.length === 0) {
    return <div>No appointments found in the system.</div>;
  }

  return (
    <div className="admin-appointment-management-page">
      <h2>Manage All Appointments</h2>
      {message && <div className="message success">{message}</div>}
      {error && <div className="message error">{error}</div>}

      <div className="appointment-list-container"> {/* Reusing styles */}
        {appointments.map((appt) => (
          <div key={appt.id} className="appointment-card"> {/* Reusing styles */}
            <h3>Appointment ID: {appt.id}</h3>
            <p><strong>Patient:</strong> {appt.patientEmail} (ID: {appt.patientId})</p>
            <p><strong>Therapist:</strong> {appt.therapistEmail} (ID: {appt.therapistId})</p>
            <p><strong>Time:</strong> {new Date(appt.appointmentTime).toLocaleString()}</p>
            <p><strong>Current Status:</strong> <span className={`status-${appt.status.toLowerCase()}`}>{appt.status}</span></p>
            
            <div className="appointment-actions">
              {/* Dropdown for status update */}
              <select 
                value={appt.status} 
                onChange={(e) => handleStatusUpdate(appt.id, e.target.value)} 
                className="status-dropdown"
              >
                <option value="PENDING">PENDING</option>
                <option value="CONFIRMED">CONFIRMED</option>
                <option value="CANCELED">CANCELED</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
            </div>
          </div>
        ))}
      </div>
      <button onClick={() => navigate('/admin/dashboard')} className="btn-secondary">Back to Dashboard</button>
    </div>
  );
};

export default AdminAppointmentManagementPage;