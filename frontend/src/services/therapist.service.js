// frontend/src/services/therapist.service.js

import axios from 'axios';
import authHeader from './authHeader.js';

const API_BASE_URL = 'http://localhost:8081/api/';

class TherapistService {
    // Public endpoint for patients to get all therapists
    async getAllTherapists() {
        const response = await axios.get(API_BASE_URL + 'patients/therapists', { headers: authHeader() });
        return response.data;
    }

    // Public endpoint for patients to get a specific therapist profile
    async getTherapistProfile(therapistId) {
        const response = await axios.get(API_BASE_URL + `patients/therapists/${therapistId}/profile`, { headers: authHeader() });
        return response.data;
    }

    // Public endpoint for patients to get a therapist's schedules
    async getTherapistSchedules(therapistId) {
        const response = await axios.get(API_BASE_URL + `therapists/${therapistId}/schedule`);
        return response.data;
    }

    // --- NEW METHOD: Get current therapist's own schedules (secured by JWT) ---
    async getOwnTherapistSchedules() {
            // API: GET /api/therapists/appointments (backend endpoint is /api/therapists/appointments but the content should be for schedules)
            // IMPORTANT: The backend API you have for GET /api/therapists/appointments fetches appointments.
            // We need an endpoint on backend for GET /api/therapists/schedule (without ID) for OWN SCHEDULES
            // Let's assume you will create a backend endpoint: GET /api/therapists/my-schedule or GET /api/therapists/schedule
            // For now, let's target GET /api/therapists/schedule (which current backend code has as a public one with ID)
            // We need a specific endpoint for authenticated therapist to get own schedule without ID.
            // If not, we could adapt: GET /api/therapists/{therapistId}/schedule and pass current user ID.

            // Let's assume you will create a backend endpoint GET /api/therapists/me/schedule
            // For now, let's use the existing GET /api/therapists/{therapistId}/schedule BUT pass the logged in therapist ID
            // This implies you might need the therapist ID from current user, which is in AuthContext.
            // It's better to update backend to have GET /api/therapists/schedule that infers ID from JWT.

            // FOR NOW, use the backend API: GET /api/therapists/{therapistId}/schedule but you will get therapistId from AuthContext
            // This means you will need to pass therapistId from the component.
            // Let's update this API call to implicitly get therapist ID from backend.
            const response = await axios.get(API_BASE_URL + `therapists/schedule`, { headers: authHeader() });
            // Backend needs to implement: @GetMapping("/schedule") @PreAuthorize("hasRole('THERAPIST')") ResponseEntity<?> getOwnTherapistSchedule();
            return response.data;
        }
        // --- END NEW METHOD ---

    // --- UPDATED METHOD: Add therapist schedule (secured by JWT) ---
    async addTherapistSchedule(scheduleRequest) {
            const response = await axios.post(API_BASE_URL + 'therapists/schedule', scheduleRequest, { headers: authHeader() });
            return response.data;
        }
        // --- END UPDATED METHOD ---

    // --- NEW METHOD: Delete therapist schedule (secured by JWT) ---
    async deleteTherapistSchedule(scheduleId) {
            const response = await axios.delete(API_BASE_URL + `therapists/schedule/${scheduleId}`, { headers: authHeader() });
            return response.data;
        }
        // --- END NEW METHOD ---
}

export default new TherapistService();