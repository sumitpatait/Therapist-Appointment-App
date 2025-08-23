// frontend/src/services/AppointmentService.js

import axios from 'axios';
import authHeader from './authHeader.js'; // Assuming this file exists and handles authentication headers

const API_BASE_URL = 'http://localhost:8081/api/'; // Your backend base URL

class AppointmentService {
    async bookAppointment(therapistId, appointmentTime) {
        const response = await axios.post(API_BASE_URL + 'patients/appointments', { therapistId, appointmentTime }, { headers: authHeader() });
        return response.data;
    }

    // Get patient's own appointments (secured)
    async getPatientAppointments() {
        const response = await axios.get(API_BASE_URL + 'patients/appointments', { headers: authHeader() });
        return response.data;
    }

    // Submit a review for an appointment (secured)
    // appointment.service.js
    async submitReview(appointmentId, rating, comment) {
        const response = await axios.post(
            API_BASE_URL + `patients/reviews`, { appointmentId, rating, comment }, // Updated: send appointmentId in the body
            { headers: authHeader() }
        );
        return response.data;
    }


    // --- NEW METHOD: Get therapist's own appointments (secured) ---
    async getTherapistAppointments() {
            // Backend GET /api/therapists/appointments already fetches for authenticated therapist
            const response = await axios.get(API_BASE_URL + 'therapists/appointments', { headers: authHeader() });
            return response.data;
        }
        // --- END NEW METHOD ---

    // --- NEW METHOD: Update appointment status (secured by therapist) ---
    async updateAppointmentStatus(appointmentId, newStatus) {
            const response = await axios.put(API_BASE_URL + `therapists/appointments/${appointmentId}/status`, { status: newStatus }, // Backend expects {"status": "CONFIRMED"} etc.
                { headers: authHeader() }
            );
            return response.data;
        }
        // --- END NEW METHOD ---
}

export default new AppointmentService();