// frontend/src/pages/DashboardPage.jsx

import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './DashboardPage.css';
import UserService from '../services/user.service'; // Import the new UserService

const DashboardPage = () => {
  const { currentUser } = useAuth();
  const [userDetails, setUserDetails] = useState(null);
  const [loadingDetails, setLoadingDetails] = useState(true);
  const [errorDetails, setErrorDetails] = useState(null);

  useEffect(() => {
    const fetchDetails = async () => {
      if (!currentUser || !currentUser.id) {
        // If no current user or ID, we can't fetch details
        setErrorDetails('User not authenticated or ID not available.');
        setLoadingDetails(false);
        return;
      }

      try {
        // Call the UserService to get the full profile
        // This endpoint should return a DTO with all user-specific fields
        const data = await UserService.getUserProfile(); 
        setUserDetails(data);
      } catch (err) {
        console.error('Failed to fetch user details:', err);
        setErrorDetails(err.response?.data?.message || 'Failed to load profile details. Please try again.');
      } finally {
        setLoadingDetails(false);
      }
    };

    fetchDetails();
  }, [currentUser]); // Re-run effect if currentUser changes (e.g., after login/logout)

  // Render access denied if no user or role is somehow missing
  if (!currentUser || !currentUser.role) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-card">
          <h2>Access Denied</h2>
          <p>Please log in to view your dashboard.</p>
          <Link to="/login" className="btn-primary">Go to Login</Link>
        </div>
      </div>
    );
  }

  if (loadingDetails) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-card">
          <h2>Loading Dashboard...</h2>
          <p>Fetching your profile details.</p>
        </div>
      </div>
    );
  }

  if (errorDetails) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-card message error">
          <h2>Error Loading Profile</h2>
          <p>{errorDetails}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-page">
      <div className="dashboard-card">
        <h2>Welcome to Your Dashboard!</h2>
        
        {/* Display common user info */}
        <div className="user-info-section">
          {userDetails?.profilePictureUrl ? (
            <img src={userDetails.profilePictureUrl} alt="Profile" className="profile-picture" />
          ) : (
            <div className="profile-picture-placeholder">No Image</div>
          )}
          <p><strong>Email:</strong> {currentUser.email}</p>
          <p><strong>Role:</strong> {currentUser.role}</p>
          
          {/* Conditional rendering for patient-specific details */}
          {currentUser.role === 'PATIENT' && userDetails && (
            <div className="details-block">
              <p><strong>Name:</strong> {userDetails.patientName}</p>
              <p><strong>Age:</strong> {userDetails.patientAge}</p>
              <p><strong>Mobile:</strong> {userDetails.patientMobileNumber}</p>
            </div>
          )}

          {/* Conditional rendering for therapist-specific details */}
          {currentUser.role === 'THERAPIST' && userDetails && (
            <div className="details-block">
              <p><strong>Bio:</strong> {userDetails.bio}</p>
              <p><strong>Experience:</strong> {userDetails.experience}</p>
              <p><strong>Qualifications:</strong> {userDetails.qualifications}</p>
              <p><strong>Specializations:</strong> {userDetails.specializationNames?.join(', ') || 'N/A'}</p>
            </div>
          )}
        </div>

        {/* Conditional content based on role */}
        {currentUser.role === 'PATIENT' && (
          <div className="role-specific-section">
            <h3>Patient Overview</h3>
            <p>Connect with therapists, manage your appointments, and track your well-being.</p>
            <div className="dashboard-links">
              <Link to="/therapists" className="btn-primary">Browse Therapists</Link>
              <Link to="/my-appointments" className="btn-primary">My Appointments</Link>
              {/* <Link to="/patient/profile" className="btn-primary">My Profile</Link> */}
            </div>
          </div>
        )}

        {currentUser.role === 'THERAPIST' && (
          <div className="role-specific-section">
            <h3>Therapist Practice Hub</h3>
            <p>Manage your schedule, view client appointments, and update your professional profile.</p>
            <div className="dashboard-links">
              <Link to="/therapist/schedule" className="btn-primary">Manage My Schedule</Link>
              <Link to="/therapist/appointments" className="btn-primary">View My Appointments</Link>
              <Link to="/therapist/dashboard" className="btn-primary">My Practice Dashboard</Link>
            </div>
          </div>
        )}

        {currentUser.role === 'ADMIN' && (
          <div className="role-specific-section">
            <h3>Admin Control Panel</h3>
            <p>Oversee user accounts, moderate content, and view application statistics.</p>
            <div className="dashboard-links">
              <Link to="/admin/users" className="btn-primary">Manage Users</Link>
              <Link to="/admin/reviews" className="btn-primary">Moderate Reviews</Link>
              <Link to="/admin/stats" className="btn-primary">View Statistics</Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;
