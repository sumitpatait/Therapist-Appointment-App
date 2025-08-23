import React, { useState, useEffect } from 'react';
import TherapistService from '../services/therapist.service.js';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import './TherapistListPage.css';

const TherapistListPage = () => {
  const [therapists, setTherapists] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { currentUser } = useAuth();

  useEffect(() => {
    const fetchTherapists = async () => {
      if (!currentUser || !currentUser.token) {
        setError('Authentication required. Please log in.');
        setLoading(false);
        return;
      }
      try {
        const data = await TherapistService.getAllTherapists();
        setTherapists(data);
      } catch (err) {
        console.error('Failed to fetch therapists:', err);
        setError('Failed to load therapists. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchTherapists();
  }, [currentUser]);

  if (loading) return <div className="loading">Loading therapists...</div>;
  if (error) return <div className="message error">{error}</div>;
  if (therapists.length === 0) return <div className="message info">No therapists found.</div>;

  return (
    <div className="therapist-list-container">
      <h2 className="therapist-list-title">Browse Our Therapists</h2>
      <div className="therapist-cards-grid">
        {therapists.map((therapist) => (
          <div key={therapist.id} className="therapist-card">
            <h3 className="therapist-card-name">{therapist.email}</h3>
            <p className="therapist-card-bio"><strong>Bio:</strong> {therapist.bio}</p>
            <p><strong>Experience:</strong> {therapist.experience}</p>
            <p><strong>Qualifications:</strong> {therapist.qualifications}</p>
            <p><strong>Specializations:</strong> {therapist.specializations ? therapist.specializations.join(', ') : 'N/A'}</p>
            <Link to={`/therapists/${therapist.id}`} className="btn-primary btn-view-profile">View Profile & Book</Link>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TherapistListPage;
