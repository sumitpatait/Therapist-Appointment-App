// frontend/src/pages/AdminUserManagementPage.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import AdminService from '../services/admin.service.js';
import { useNavigate, Link } from 'react-router-dom'; // <--- IMPORT Link HERE
import './AdminUserManagementPage.css';

const AdminUserManagementPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  const fetchUsers = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'ADMIN') {
      setError('Authentication or Admin role required.');
      setLoading(false);
      return;
    }
    try {
      const data = await AdminService.getAllUsers();
      setUsers(data);
    } catch (err) {
      console.error('Failed to fetch users:', err);
      setError(err.response?.data || 'Failed to load users. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [currentUser]);

  useEffect(() => {
    fetchUsers();
  }, [currentUser, navigate, fetchUsers]);

  // --- MODIFIED: handleViewUser to navigate ---
  const handleViewUser = (userId) => {
    navigate(`/admin/users/${userId}`); // Navigate to the new detail page
  };
  // --- END MODIFIED ---

  const handleDeleteUser = async (userId) => {
    if (window.confirm(`Are you sure you want to delete user ID: ${userId}? This action is irreversible.`)) {
      try {
        await AdminService.deleteUser(userId);
        alert(`User ID: ${userId} deleted successfully.`);
        fetchUsers();
      } catch (err) {
        console.error('Failed to delete user:', err);
        alert(err.response?.data || 'Failed to delete user.');
      }
    }
  };

  if (loading) {
    return <div>Loading users...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (users.length === 0) {
    return <div>No users found in the system.</div>;
  }

  return (
    <div className="admin-user-management-page">
      <h2>Manage All Users</h2>
      <div className="user-list-container">
        {users.map((user) => (
          <div key={user.id} className="user-card">
            <h3>ID: {user.id}</h3>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Role:</strong> {user.role}</p>
            <div className="user-actions">
              <button onClick={() => handleViewUser(user.id)} className="btn-primary small-button">View</button> {/* This button remains */}
              <button onClick={() => handleDeleteUser(user.id)} className="btn-secondary small-button">Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminUserManagementPage;