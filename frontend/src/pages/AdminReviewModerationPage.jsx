// frontend/src/pages/AdminReviewModerationPage.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import AdminService from '../services/admin.service.js'; // Import Admin Service
import { useNavigate } from 'react-router-dom';
import './AdminReviewModerationPage.css';


const AdminReviewModerationPage = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(''); // For success/error messages
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  // Function to fetch all reviews
  const fetchReviews = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Authentication or Admin role required to moderate reviews.');
      setLoading(false);
      return;
    }
    try {
      const data = await AdminService.getAllReviews(); // Calls backend GET /api/admin/reviews
      setReviews(data);
    } catch (err) {
      console.error('Failed to fetch reviews:', err);
      setError(err.response?.data || 'Failed to load reviews. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [currentUser]); // Depend on currentUser for re-fetching

  useEffect(() => {
    fetchReviews(); // Initial fetch when component mounts
  }, [fetchReviews]); // fetchReviews is wrapped in useCallback

  // Handle delete review
  const handleDeleteReview = async (reviewId) => {
    setMessage('');
    setError(null);
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Unauthorized action. Please log in as an admin.');
      return;
    }
    if (window.confirm(`Are you sure you want to delete review ID: ${reviewId}? This action is irreversible.`)) {
      try {
        await AdminService.deleteReview(reviewId); // Calls backend DELETE /api/admin/reviews/{id}
        setMessage(`Review ID: ${reviewId} deleted successfully.`);
        fetchReviews(); // Refresh list
      } catch (err) {
        console.error('Failed to delete review:', err);
        setError(err.response?.data || 'Failed to delete review. Please try again.');
      }
    }
  };

  if (loading) {
    return <div>Loading reviews...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (reviews.length === 0) {
    return <div>No reviews found in the system.</div>;
  }

  return (
    <div className="admin-review-moderation-page">
      <h2>Moderate Reviews</h2>
      {message && <div className="message success">{message}</div>}
      {error && <div className="message error">{error}</div>}

      <div className="review-list-container">
        {reviews.map((review) => (
          <div key={review.id} className="review-card">
            <h3>Review ID: {review.id}</h3>
            <p><strong>Appointment ID:</strong> {review.appointmentId}</p>
            <p><strong>Patient:</strong> {review.patientEmail}</p>
            <p><strong>Therapist:</strong> {review.therapistEmail}</p>
            <p><strong>Rating:</strong> {review.rating} / 5</p>
            <p><strong>Comment:</strong> {review.comment || 'No comment provided.'}</p>
            <p><strong>Date:</strong> {new Date(review.reviewDate).toLocaleDateString()}</p>
            <div className="review-actions">
              <button onClick={() => handleDeleteReview(review.id)} className="btn-secondary small-button">Delete Review</button>
            </div>
          </div>
        ))}
      </div>
      <button onClick={() => navigate('/admin/dashboard')} className="btn-secondary">Back to Dashboard</button>
    </div>
  );
};

export default AdminReviewModerationPage;