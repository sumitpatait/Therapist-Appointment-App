// frontend/src/components/Navbar.jsx

import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">My Therapist App</Link>
      <div className="navbar-nav">
        {currentUser ? (
          <>
            <span className="nav-item">Welcome, {currentUser.email} ({currentUser.role})</span>
            <Link to="/dashboard" className="nav-item">Dashboard</Link>
            {currentUser.role === 'PATIENT' && (
              <>
                <Link to="/therapists" className="nav-item">Browse Therapists</Link>
                <Link to="/my-appointments" className="nav-item">My Appointments</Link>
              </>
            )}
            {currentUser.role === 'THERAPIST' && (
              <>
                 <Link to="/therapist/dashboard" className="nav-item">My Practice</Link>
                 <Link to="/therapist/appointments" className="nav-item">View My Appointments</Link>
              </>
            )}
            {currentUser.role === 'ADMIN' && ( // <--- Add this section
                 <Link to="/admin/dashboard" className="nav-item">Admin Panel</Link>
            )}
            <button onClick={handleLogout} className="nav-item nav-button">Logout</button>
          </>
        ) : (
          <>
            <Link to="/login" className="nav-item">Login</Link>
            <Link to="/register" className='nav-item'>Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;