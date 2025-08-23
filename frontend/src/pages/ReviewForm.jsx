// frontend/src/components/ReviewForm.jsx

import React, { useState } from 'react';
import AppointmentService from '../services/appointment.service.js'; // Import appointment service
import './ReviewForm.css';

const ReviewForm = ({ appointmentId, onClose }) => {
  const [rating, setRating] = useState(5); // Default rating
  const [comment, setComment] = useState(''); // Changed to 'comment' to match backend DTO
  const [submissionMessage, setSubmissionMessage] = useState('');
  const [submissionError, setSubmissionError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmissionMessage('');
    setSubmissionError('');

    try {
      // Backend: POST /api/patients/appointments/{appointmentId}/reviews
      const response = await AppointmentService.submitReview(appointmentId, rating, comment); // Pass 'comment' here
      setSubmissionMessage('Review submitted successfully!');
      // Optionally, refresh the appointments list in parent component
      setTimeout(() => {
        onClose(); // Close the form after a short delay to show message
      }, 1500); 
    } catch (err) {
      console.error('Review submission failed:', err);
      // Backend error response.data might be a string (e.g., "A review for this appointment already exists.")
      // or a more complex error object.
      setSubmissionError(err.response?.data || 'Failed to submit review. Please try again.');
    }
  };

  return (
    <div className="review-form-overlay">
      <div className="review-form">
        <h2>Leave a Review for Appointment ID: {appointmentId}</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="rating">Rating:</label>
            <select id="rating" value={rating} onChange={(e) => setRating(parseInt(e.target.value, 10))} className="form-control">
              <option value={1}>1 Star</option>
              <option value={2}>2 Stars</option>
              <option value={3}>3 Stars</option>
              <option value={4}>4 Stars</option>
              <option value={5}>5 Stars</option>
            </select>
          </div>
          <div className="form-group">
            <label htmlFor="comment">Comment:</label>
            <textarea id="comment" value={comment} onChange={(e) => setComment(e.target.value)} rows="4" className="form-control" required />
          </div>
          <button type="submit" className="btn-primary">Submit Review</button>
          {submissionMessage && <div className="message success">{submissionMessage}</div>}
          {submissionError && <div className="message error">{submissionError}</div>}
        </form>
        <button onClick={onClose} className="btn-secondary">Close</button>
      </div>
    </div>
  );
};

export default ReviewForm;