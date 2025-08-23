import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AdminService from '../services/admin.service.js';
import './AdminUserDetailPage.css';

const AdminUserDetailPage = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const { currentUser } = useAuth();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUser = async () => {
      if (!currentUser || currentUser.role !== 'ADMIN') {
        setError('Authentication or Admin role required to view user details.');
        setLoading(false);
        return;
      }
      try {
        const data = await AdminService.getUserById(userId);
        setUser(data);
      } catch (err) {
        console.error(`Failed to fetch user ${userId} details:`, err);
        setError(err.response?.data || `Failed to load user details for ID: ${userId}.`);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [userId, currentUser]);

  if (loading) {
    return <div>Loading user details...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (!user) {
    return <div>User with ID {userId} not found.</div>;
  }

  return (
    <div className="admin-user-detail-page">
      <h2>User Details (ID: {user.id})</h2>
      
      {/* Display common user details */}
      <div className="user-details-card">
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Role:</strong> {user.role}</p>

          {/* Conditionally render details based on role */}
          {user.role === 'PATIENT' && (
              <div className="patient-details">
                  <h3>Patient Specific Details</h3>
                  <p><strong>Name:</strong> {user.patientName}</p>
                  <p><strong>Age:</strong> {user.patientAge}</p>
                  <p><strong>Mobile Number:</strong> {user.patientMobileNumber}</p>
              </div>
          )}

          {user.role === 'THERAPIST' && (
              <div className="therapist-details">
                  <h3>Therapist Specific Details</h3>
                  <p><strong>Bio:</strong> {user.bio}</p>
                  <p><strong>Experience:</strong> {user.experience}</p>
                  <p><strong>Qualifications:</strong> {user.qualifications}</p>
                  <p><strong>Specializations:</strong> {user.specializationNames ? user.specializationNames.join(', ') : 'N/A'}</p>
              </div>
          )}
      </div>
      
      <button onClick={() => navigate('/admin/users')} className="btn-secondary">Back to User List</button>
    </div>
  );
};

export default AdminUserDetailPage;
