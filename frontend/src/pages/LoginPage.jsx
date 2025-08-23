// frontend/src/pages/LoginPage.jsx
// frontend/src/pages/LoginPage.jsx

import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // <--- ADD Link HERE
import { useAuth } from '../context/AuthContext';
import './LoginPage.css';

// import Button from 'react-bootstrap/Button';

// ... rest of your code ...
// ... rest of your code ...
const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage(''); // Clear previous messages
    try {
      await login(email, password);
      navigate('/dashboard'); // Redirect to dashboard on successful login
    } catch (error) {
      console.error('Login error:', error);
      setMessage(error.response?.data || 'Login failed. Please check your credentials.');
    }
  };

  return (
    <div className="auth-form">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn-primary">Login</button>
        {message && <div className="message error">{message}</div>}
      </form>
      <p>Don't have an account? <Link to="/register" >Register here</Link>.</p>
    </div>
  );
};

export default LoginPage;