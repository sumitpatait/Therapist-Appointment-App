// frontend/src/pages/RegisterPage.jsx

import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService'; // Direct service call for register
import './RegisterPage.css';


const RegisterPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('PATIENT'); // Default role for registration
  const [bio, setBio] = useState('');
  const [experience, setExperience] = useState('');
  const [qualifications, setQualifications] = useState('');
  const [specializationInput, setSpecializationInput] = useState('');
  const [patientName, setPatientName] = useState(''); // New state for patient name
  const [patientAge, setPatientAge] = useState(''); // New state for patient age
  const [patientMobileNumber, setPatientMobileNumber] = useState(''); // New state for patient mobile number
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    try {
      let response;
      if (role === 'PATIENT') {
        // Updated to pass new patient fields
        response = await AuthService.registerPatient(email, password, patientName, patientAge, patientMobileNumber);
      } else if (role === 'THERAPIST') {
        const specializationNames = specializationInput.split(',').map(s => s.trim()).filter(s => s.length > 0);
        response = await AuthService.registerTherapist(email, password, bio, experience, qualifications, specializationNames);
      }
      setMessage(response); // Backend returns a success message string
      navigate('/login'); // Redirect to login after successful registration
    } catch (error) {
      console.error('Registration error:', error);
      setMessage(error.response?.data || 'Registration failed.');
    }
  };

  return (
    <div className="auth-form">
      <h2>Register</h2>
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
        <div className="form-group">
          <label htmlFor="role">Register as:</label>
          <select id="role" value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="PATIENT">Patient</option>
            <option value="THERAPIST">Therapist</option>
          </select>
        </div>

        {role === 'PATIENT' && (
          <>
            <h3>Patient Details</h3>
            <div className="form-group">
              <label htmlFor="patientName">Name:</label>
              <input type="text" id="patientName" value={patientName} onChange={(e) => setPatientName(e.target.value)} required />
            </div>
            <div className="form-group">
              <label htmlFor="patientAge">Age:</label>
              <input type="number" id="patientAge" value={patientAge} onChange={(e) => setPatientAge(e.target.value)} required />
            </div>
            <div className="form-group">
              <label htmlFor="patientMobileNumber">Mobile Number:</label>
              <input type="tel" id="patientMobileNumber" value={patientMobileNumber} onChange={(e) => setPatientMobileNumber(e.target.value)} required />
            </div>
          </>
        )}

        {role === 'THERAPIST' && (
          <>
            <h3>Therapist Profile Details</h3>
            <div className="form-group">
              <label htmlFor="bio">Bio:</label>
              <textarea id="bio" value={bio} onChange={(e) => setBio(e.target.value)} rows="3"></textarea>
            </div>
            <div className="form-group">
              <label htmlFor="experience">Experience:</label>
              <input type="text" id="experience" value={experience} onChange={(e) => setExperience(e.target.value)} />
            </div>
            <div className="form-group">
              <label htmlFor="qualifications">Qualifications:</label>
              <input type="text" id="qualifications" value={qualifications} onChange={(e) => setQualifications(e.target.value)} />
            </div>
            <div className="form-group">
              <label htmlFor="specializations">Specializations (comma-separated):</label>
              <input type="text" id="specializations" value={specializationInput} onChange={(e) => setSpecializationInput(e.target.value)} placeholder="e.g., Anxiety, Depression" />
            </div>
          </>
        )}

        <button type="submit" className="btn-primary">Register</button>
        {message && <div className="message success">{message}</div>}
      </form>
      <p>Already have an account? <Link to="/login">Login here</Link>.</p>
    </div>
  );
};

export default RegisterPage;
