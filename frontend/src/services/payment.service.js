// frontend/src/services/payment.service.js

import axios from 'axios';
import authHeader from './authHeader.js';

const API_BASE_URL = 'http://localhost:8081/api/'; // Your backend base URL

class PaymentService {
    // Call backend to create Razorpay Order
    async createRazorpayOrder(orderRequest) {
        const response = await axios.post(API_BASE_URL + 'payments/create_order', orderRequest, { headers: authHeader() });
        return response.data; // Returns RazorpayOrderResponse
    }

    // Call backend to verify payment signature after successful frontend payment
    async verifyPaymentSignature(verifyRequest) {
        const response = await axios.post(API_BASE_URL + 'payments/verify_signature', verifyRequest, { headers: authHeader() });
        return response.data; // Returns success message or error
    }
}

export default new PaymentService();