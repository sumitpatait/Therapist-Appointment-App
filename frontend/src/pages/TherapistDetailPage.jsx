// frontend/src/pages/TherapistDetailPage.jsx

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import TherapistService from '../services/therapist.service.js';
import AppointmentService from '../services/appointment.service.js';
import { useAuth } from '../context/AuthContext';
import './TherapistDetailPage.css';

const TherapistDetailPage = () => {
  const { therapistId } = useParams();
  const navigate = useNavigate();
  const { currentUser } = useAuth();

  const [therapist, setTherapist] = useState(null);
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [bookingMessage, setBookingMessage] = useState('');
  const [selectedAppointmentTime, setSelectedAppointmentTime] = useState('');

  useEffect(() => {
    const fetchTherapistData = async () => {
      if (!currentUser || !currentUser.token) {
        setError('Authentication required. Please log in.');
        setLoading(false);
        return;
      }
      try {
        const therapistData = await TherapistService.getTherapistProfile(therapistId);
        setTherapist(therapistData);

        const scheduleData = await TherapistService.getTherapistSchedules(therapistId);
        setSchedules(scheduleData);

      } catch (err) {
        console.error('Failed to fetch therapist data:', err);
        setError(err.response?.data || 'Failed to load therapist profile or schedule.');
      } finally {
        setLoading(false);
      }
    };

    fetchTherapistData();
  }, [therapistId, currentUser]);

  const handleBookAppointment = async () => {
    if (!selectedAppointmentTime) {
      setBookingMessage('Please select a date and time for the appointment.');
      return;
    }
    setBookingMessage('');
    try {
      // Ensure the response.data is clearly consumed
      const bookedAppointmentData = await AppointmentService.bookAppointment(therapistId, selectedAppointmentTime);
      setBookingMessage(`Appointment booked successfully! Status: ${bookedAppointmentData.status || 'PENDING'}`); // Use status from response
    } catch (err) {
      console.error('Appointment booking failed:', err);
      setBookingMessage(err.response?.data || 'Failed to book appointment. Please try again.');
    }
  };

  if (loading) {
    return <div>Loading therapist details...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (!therapist) {
    return <div>Therapist not found.</div>;
  }

  return (
    <div className="therapist-detail-page">
      <h2>{therapist.email}'s Profile</h2>
      <p><strong>Bio:</strong> {therapist.bio}</p>
      <p><strong>Experience:</strong> {therapist.experience}</p>
      <p><strong>Qualifications:</strong> {therapist.qualifications}</p>
      <p><strong>Specializations:</strong> {therapist.specializations ? therapist.specializations.join(', ') : 'N/A'}</p>

      <h3>Available Schedule Slots</h3>
      {schedules.length === 0 ? (
        <p>No available schedule slots found for this therapist.</p>
      ) : (
        <div className="schedule-list">
          {schedules.map((slot) => (
            <div key={slot.id} className="schedule-slot-card">
              <p><strong>Day:</strong> {slot.dayOfWeek}</p>
              <p><strong>Time:</strong> {slot.startTime} - {slot.endTime}</p>
            </div>
          ))}
        </div>
      )}

      <h3>Book an Appointment</h3>
      {currentUser.role === 'PATIENT' ? (
        <div className="booking-form">
          <label htmlFor="appointmentTime">Select Date & Time:</label>
          <input
            type="datetime-local"
            id="appointmentTime"
            value={selectedAppointmentTime}
            onChange={(e) => setSelectedAppointmentTime(e.target.value)}
            required
          />
          <button onClick={handleBookAppointment} className="btn-primary">Book Now</button>
          {bookingMessage && <div className={`message ${bookingMessage.includes('successfully') ? 'success' : 'error'}`}>{bookingMessage}</div>}
        </div>
      ) : (
        <p>Only patients can book appointments. Please log in as a patient.</p>
      )}

      <button onClick={() => navigate('/therapists')} className="btn-secondary">Back to Therapist List</button>
    </div>
  );
};

export default TherapistDetailPage;