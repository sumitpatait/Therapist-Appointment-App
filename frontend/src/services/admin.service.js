// frontend/src/services/admin.service.js

import axios from 'axios';
import authHeader from './authHeader.js';

const API_BASE_URL = 'http://localhost:8081/api/';

class AdminService {
    async getAllUsers() {
        const response = await axios.get(API_BASE_URL + 'admin/users', { headers: authHeader() });
        return response.data;
    }

    async getUserById(userId) {
        const response = await axios.get(API_BASE_URL + `admin/users/${userId}`, { headers: authHeader() });
        return response.data;
    }

    async deleteUser(userId) {
        const response = await axios.delete(API_BASE_URL + `admin/users/${userId}`, { headers: authHeader() });
        return response.data;
    }

    async getAllAppointments() {
        const response = await axios.get(API_BASE_URL + 'admin/appointments', { headers: authHeader() });
        return response.data;
    }

    async updateAppointmentStatus(appointmentId, newStatus) {
        const response = await axios.put(API_BASE_URL + `admin/appointments/${appointmentId}/status`, { status: newStatus }, { headers: authHeader() });
        return response.data;
    }

    async getAllReviews() {
        const response = await axios.get(API_BASE_URL + 'admin/reviews', { headers: authHeader() });
        return response.data;
    }

    async deleteReview(reviewId) {
        const response = await axios.delete(API_BASE_URL + `admin/reviews/${reviewId}`, { headers: authHeader() });
        return response.data;
    }

    // --- NEW METHOD: Get system statistics (for Admin) ---
    async getSystemStatistics() {
            const response = await axios.get(API_BASE_URL + 'admin/stats', { headers: authHeader() });
            return response.data;
        }
        // --- END NEW METHOD ---
}

export default new AdminService();