// frontend/src/pages/TherapistAppointmentsManagerPage.jsx

import React, { useState, useEffect, useCallback } from 'react'; // <--- IMPORT useCallback
import { useAuth } from '../context/AuthContext';
import AppointmentService from '../services/appointment.service.js';
import { useNavigate } from 'react-router-dom';
import './TherapistAppointmentsManagerPage.css';

const TherapistAppointmentsManagerPage = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  // Wrap fetchAppointments with useCallback
  const fetchAppointments = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'THERAPIST') {
      setError('Authentication or Therapist role required to view appointments.');
      setLoading(false);
      return;
    }
    try {
      const data = await AppointmentService.getTherapistAppointments();
      setAppointments(data);
    } catch (err) {
      console.error('Failed to fetch therapist appointments:', err);
      setError(err.response?.data || 'Failed to load appointments. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [currentUser]); // <-- Dependencies for useCallback: fetchAppointments depends on currentUser

  useEffect(() => {
    fetchAppointments();
  }, [currentUser, navigate, fetchAppointments]); // <-- Keep fetchAppointments in useEffect dependencies

  const handleStatusUpdate = async (appointmentId, newStatus) => {
    setMessage('');
    setError(null);
    if (!currentUser || currentUser.role !== 'THERAPIST') {
      setError('Unauthorized action. Please log in as a therapist.');
      return;
    }
    try {
      const _response = await AppointmentService.updateAppointmentStatus(appointmentId, newStatus);
      setMessage(`Appointment ${appointmentId} updated to ${newStatus} successfully!`);
      fetchAppointments(); // Refresh the list
    } catch (err) {
      console.error(`Failed to update appointment ${appointmentId} status:`, err);
      setMessage('');
      setError(err.response?.data || 'Failed to update appointment status. Please try again.');
    }
  };

  if (loading) {
    return <div>Loading appointments...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (appointments.length === 0) {
    return <div>You have no appointments scheduled yet.</div>;
  }

  return (
    <div className="therapist-appointments-manager-page">
      <h2>Manage My Appointments</h2>
      {message && <div className="message success">{message}</div>}
      {error && <div className="message error">{error}</div>}

      <div className="appointment-list-container">
        {appointments.map((appt) => (
          <div key={appt.id} className="appointment-card">
            <h3>Appointment ID: {appt.id}</h3>
            <p><strong>Patient:</strong> {appt.patientEmail}</p>
            <p><strong>Time:</strong> {new Date(appt.appointmentTime).toLocaleString()}</p>
            <p><strong>Status:</strong> <span className={`status-${appt.status.toLowerCase()}`}>{appt.status}</span></p>

            {appt.status === 'PENDING' && (
              <div className="appointment-actions">
                <button
                  onClick={() => handleStatusUpdate(appt.id, 'CONFIRMED')}
                  className="btn-primary small-button"
                >
                  Accept
                </button>
                <button
                  onClick={() => handleStatusUpdate(appt.id, 'CANCELED')}
                  className="btn-secondary small-button"
                >
                  Reject
                </button>
              </div>
            )}
            {appt.status === 'CONFIRMED' && new Date(appt.appointmentTime) < new Date() && (
                 <button
                  onClick={() => handleStatusUpdate(appt.id, 'COMPLETED')}
                  className="btn-primary small-button"
                >
                  Mark as Completed
                </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default TherapistAppointmentsManagerPage;