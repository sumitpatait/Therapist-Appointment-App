// frontend/src/components/RazorpayCheckout.jsx

import React, { useEffect } from 'react';
import './RazorpayCheckout.css';


// Make sure Razorpay's checkout.js is loaded in public/index.html head
// <script src="https://checkout.razorpay.com/v1/checkout.js"></script>

const RazorpayCheckout = ({ order, userEmail, onPaymentCallback }) => {
  useEffect(() => {
    const loadRazorpayScript = () => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.async = true;
      script.onload = () => {
        openRazorpayCheckout();
      };
      document.body.appendChild(script);
    };

    const openRazorpayCheckout = () => {
      const options = {
        key: order.keyId, // Your Key ID from backend response
        amount: order.amount * 100, // Amount in paisa, from backend response (converted to paisa)
        currency: order.currency, // Currency code from backend response
        name: 'My Therapist App',
        description: 'Appointment Booking Payment',
        order_id: order.orderId, // Order ID from backend response
        handler: function (response) {
          // This function is called when the payment is successful
          onPaymentCallback('success', {
            razorpay_payment_id: response.razorpay_payment_id,
            razorpay_order_id: response.razorpay_order_id,
            razorpay_signature: response.razorpay_signature
          });
        },
        prefill: {
          email: userEmail, // Patient's email
          // contact: '9999999999' // Optional: Patient's phone number
        },
        notes: {
          appointment_id: order.appointmentId, // Pass appointment ID
        },
        theme: {
          color: '#007bff' // Your app's theme color
        }
      };

      const rzp = new window.Razorpay(options);
      rzp.on('payment.failed', function (response) {
        // This function is called when the payment fails
        console.error('Razorpay payment failed:', response.error);
        onPaymentCallback('failed', response.error.description);
      });
      rzp.open();
    };

    if (window.Razorpay) { // If script already loaded
      openRazorpayCheckout();
    } else {
      loadRazorpayScript();
    }

    // Cleanup: return a function that will be called when the component unmounts
    return () => {
      // You might want to remove the script from the DOM if it's not needed globally
      // (though Razorpay script is often left globally)
    };
  }, [order, userEmail, onPaymentCallback]); // Re-run if order details or userEmail change

  return (
    <div className="razorpay-overlay">
      <div className="razorpay-loading">Opening payment gateway...</div>
    </div>
  );
};

export default RazorpayCheckout;