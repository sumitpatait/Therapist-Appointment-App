// frontend/src/components/ReviewForm.jsx

import React, { useState } from 'react';
import AppointmentService from '../services/appointment.service.js'; // Import appointment service
import './ReviewForm.css';

const ReviewForm = ({ appointmentId, onClose }) => {
  const [rating, setRating] = useState(5); // Default rating
  const [reviewText, setReviewText] = useState('');
  const [submissionMessage, setSubmissionMessage] = useState('');
  const [submissionError, setSubmissionError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmissionMessage('');
    setSubmissionError('');

    try {
      const response = await AppointmentService.submitReview(appointmentId, rating, reviewText);
      setSubmissionMessage('Review submitted successfully!');
      // Optionally, refresh the appointments list or redirect
      onClose(); // Close the form
    } catch (err) {
      console.error('Review submission failed:', err);
      setSubmissionError(err.response?.data || 'Failed to submit review. Please try again.');
    }
  };

  return (
    <div className="review-form-overlay">
      <div className="review-form">
        <h2>Leave a Review</h2>
        <form onSubmit={handleSubmit}>
          <div className="rating-input">
            <label htmlFor="rating">Rating:</label>
            <select id="rating" value={rating} onChange={(e) => setRating(parseInt(e.target.value, 10))}>
              <option value={1}>1 Star</option>
              <option value={2}>2 Stars</option>
              <option value={3}>3 Stars</option>
              <option value={4}>4 Stars</option>
              <option value={5}>5 Stars</option>
            </select>
          </div>
          <div className="review-text-input">
            <label htmlFor="reviewText">Review:</label>
            <textarea id="reviewText" value={reviewText} onChange={(e) => setReviewText(e.target.value)} required />
          </div>
          <button type="submit" className="btn-primary">Submit Review</button>
          {submissionMessage && <div className="success-message">{submissionMessage}</div>}
          {submissionError && <div className="error-message">{submissionError}</div>}
        </form>
        <button className="btn-secondary" onClick={onClose}>Close</button>
      </div>
    </div>
  );
};

export default ReviewForm;