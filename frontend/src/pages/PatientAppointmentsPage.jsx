// frontend/src/pages/PatientAppointmentsPage.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import AppointmentService from '../services/appointment.service.js';
import PaymentService from '../services/payment.service.js';
import RazorpayCheckout from './RazorpayCheckout.jsx';
import ReviewForm from './ReviewForm.jsx';
import { useNavigate } from 'react-router-dom';
import './PatientAppointmentsPage.css';

const PatientAppointmentsPage = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  // State for Razorpay Checkout
  const [showRazorpay, setShowRazorpay] = useState(false);
  const [razorpayOrderDetails, setRazorpayOrderDetails] = useState(null);
  const [currentAppointmentForPayment, setCurrentAppointmentForPayment] = useState(null);
  // State for Review Form
  const [_selectedAppointmentId, setSelectedAppointmentId] = useState(null);

  const fetchAppointments = useCallback(async () => {
    if (!currentUser || currentUser.role !== 'PATIENT') {
      setError('Authentication or Patient role required to view appointments.');
      setLoading(false);
      return;
    }
    try {
      const data = await AppointmentService.getPatientAppointments();
      setAppointments(data);
    } catch (err) {
      console.error('Failed to fetch patient appointments:', err);
      setError(err.response?.data || 'Failed to load appointments. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [currentUser]); // navigate removed from this useCallback dependency

  useEffect(() => {
    fetchAppointments();
  }, [fetchAppointments]);

  const handleLeaveReview = (appointmentId) => {
      setSelectedAppointmentId(appointmentId); // Set the state to show the ReviewForm
  };

  const handlePayNow = async (appointmentId, amount) => {
    setMessage('');
    setError(null);
    if (!currentUser) {
      setError('You must be logged in to make a payment.');
      return;
    }

    try {
      const orderRequest = {
        appointmentId: appointmentId,
        amount: amount,
        currency: "INR"
      };

      const orderResponse = await PaymentService.createRazorpayOrder(orderRequest);

      setRazorpayOrderDetails(orderResponse);
      setCurrentAppointmentForPayment(appointmentId);
      setShowRazorpay(true);

    } catch (err) {
      console.error('Failed to create Razorpay order:', err);
      setError(err.response?.data || 'Failed to initiate payment. Please try again.');
    }
  };

  const handlePaymentCallback = async (status, response) => {
    setShowRazorpay(false);
    if (status === 'success') {
      setMessage('Payment process successful. Verifying payment...');
      setError(null);
      try {
        const verifyRequest = {
          razorpayOrderId: response.razorpay_order_id,
          razorpayPaymentId: response.razorpay_payment_id,
          razorpaySignature: response.razorpay_signature,
          appointmentId: currentAppointmentForPayment
        };
        const verificationResult = await PaymentService.verifyPaymentSignature(verifyRequest);
        setMessage(verificationResult);
        fetchAppointments(); // Refresh appointment list
        navigate('/my-appointments'); // Using navigate here
      } catch (err) {
        console.error('Payment verification failed on backend:', err);
        setError(err.response?.data || 'Payment verification failed on backend.');
      }
    } else if (status === 'failed') {
      setError('Payment failed or was cancelled.');
      setMessage('');
      navigate('/my-appointments'); // Using navigate here
    }
    setCurrentAppointmentForPayment(null);
  };

  if (loading) {
    return <div>Loading appointments...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  if (appointments.length === 0) {
    return <div>You have no appointments booked yet.</div>;
  }

  return (
    
    <div className="patient-appointments-page">
      <h2>My Appointments</h2>
      {message && <div className={`message ${message.includes('successfully') ? 'success' : error ? 'error' : ''}`}>{message}</div>}
      {error && <div className="message error">{error}</div>}

      <div className="appointment-list-container">
        {appointments.map((appt) => (
          <div key={appt.id} className="appointment-card">
            <h3>Appointment ID: {appt.id}</h3>
            <p><strong>Therapist:</strong> {appt.therapistEmail}</p>
            <p><strong>Time:</strong> {new Date(appt.appointmentTime).toLocaleString()}</p>
            <p><strong>Status:</strong> <span className={`status-${appt.status.toLowerCase()}`}>{appt.status}</span></p>

            <div className="appointment-actions">
                {appt.status === 'PENDING' && (
                    <button className="btn-secondary" onClick={() => handlePayNow(appt.id, 500.00)}>Pay Now</button>
                )}
                {appt.status === 'COMPLETED' && (
                    <button className="btn-secondary" onClick={() => handleLeaveReview(appt.id)}>Leave Review</button>
                )}
            </div>
          </div>
        ))}
      </div>

      {showRazorpay && razorpayOrderDetails && (
        <RazorpayCheckout 
          order={razorpayOrderDetails} 
          userEmail={currentUser.email}
          onPaymentCallback={handlePaymentCallback} 
        />
      )}
      {_selectedAppointmentId && ( // Render ReviewForm based on _selectedAppointmentId
        <ReviewForm appointmentId={_selectedAppointmentId} onClose={() => setSelectedAppointmentId(null)} />
      )}
    </div>
  );
};

export default PatientAppointmentsPage;