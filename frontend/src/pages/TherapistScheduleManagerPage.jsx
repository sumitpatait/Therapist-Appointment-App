// frontend/src/pages/TherapistScheduleManagerPage.jsx

import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import TherapistService from '../services/therapist.service.js';
import { useNavigate } from 'react-router-dom';
import './TherapistScheduleManagerPage.css';

const TherapistScheduleManagerPage = () => {
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');

  const [newDayOfWeek, setNewDayOfWeek] = useState('MONDAY');
  const [newStartTime, setNewStartTime] = useState('09:00');
  const [newEndTime, setNewEndTime] = useState('17:00');

  useEffect(() => {
    const fetchSchedules = async () => {
      if (!currentUser || currentUser.role !== 'THERAPIST') {
        setError('Authentication or Therapist role required.');
        setLoading(false);
        return;
      }
      try {
        const data = await TherapistService.getOwnTherapistSchedules();
        setSchedules(data);
      } catch (err) {
        console.error('Failed to fetch therapist schedules:', err);
        setError(err.response?.data || 'Failed to load schedules. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchSchedules();
  }, [currentUser, navigate]);

  const handleAddSchedule = async (e) => {
    e.preventDefault();
    setMessage('');
    setError(null);
    if (!currentUser || currentUser.role !== 'THERAPIST') {
      setError('Unauthorized action. Please log in as a therapist.');
      return;
    }
    try {
      const scheduleRequest = {
        dayOfWeek: newDayOfWeek,
        startTime: newStartTime + ':00',
        endTime: newEndTime + ':00'
      };
      // Renamed response to _response as its data is not directly used here
      const _response = await TherapistService.addTherapistSchedule(scheduleRequest); 
      setMessage('Schedule slot added successfully!');
      const updatedSchedules = await TherapistService.getOwnTherapistSchedules();
      setSchedules(updatedSchedules);
      setNewStartTime('09:00');
      setNewEndTime('17:00');
    } catch (err) {
      console.error('Failed to add schedule:', err);
      setMessage('');
      setError(err.response?.data || 'Failed to add schedule slot. Please check inputs.');
    }
  };

  const handleDeleteSchedule = async (scheduleId) => {
    setMessage('');
    setError(null);
    if (!currentUser || currentUser.role !== 'THERAPIST') {
      setError('Unauthorized action. Please log in as a therapist.');
      return;
    }
    try {
      // Renamed response to _response as its data (for DELETE 204) is not directly used
      const _response = await TherapistService.deleteTherapistSchedule(scheduleId); 
      setMessage('Schedule slot deleted successfully!');
      setSchedules(schedules.filter(s => s.id !== scheduleId));
    } catch (err) {
      console.error('Failed to delete schedule:', err);
      setMessage('');
      setError(err.response?.data || 'Failed to delete schedule slot.');
    }
  };

  if (loading) {
    return <div>Loading schedules...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (schedules.length === 0) {
    return <p>You currently have no schedule slots. Add some above!</p>;
  }

  return (
    <div className="schedule-manager-page">
      <h2>Manage My Schedule</h2>
      {message && <div className="message success">{message}</div>}
      {error && <div className="message error">{error}</div>}

      <h3>Add New Availability</h3>
      <form onSubmit={handleAddSchedule} className="add-schedule-form">
        <div className="form-group">
          <label htmlFor="dayOfWeek">Day:</label>
          <select id="dayOfWeek" value={newDayOfWeek} onChange={(e) => setNewDayOfWeek(e.target.value)}>
            {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map(day => (
              <option key={day} value={day}>{day}</option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label htmlFor="startTime">Start Time:</label>
          <input type="time" id="startTime" value={newStartTime} onChange={(e) => setNewStartTime(e.target.value)} required />
        </div>
        <div className="form-group">
          <label htmlFor="endTime">End Time:</label>
          <input type="time" id="endTime" value={newEndTime} onChange={(e) => setNewEndTime(e.target.value)} required />
        </div>
        <button type="submit" className="btn-primary">Add Slot</button>
      </form>

      <h3>Current Schedule Slots</h3>
      {schedules.length === 0 ? (
        <p>You currently have no schedule slots. Add some above!</p>
      ) : (
        <div className="schedule-list-container">
          {schedules.map((slot) => (
            <div key={slot.id} className="schedule-slot-card">
              <p><strong>Day:</strong> {slot.dayOfWeek}</p>
              <p><strong>Time:</strong> {slot.startTime} - {slot.endTime}</p>
              <button onClick={() => handleDeleteSchedule(slot.id)} className="btn-secondary delete-button">Delete</button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TherapistScheduleManagerPage;