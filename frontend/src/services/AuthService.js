// frontend/src/services/auth.service.js

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/'; // Base URL for your Spring Boot backend

class AuthService {
    // Registers a new patient
    async registerPatient(email, password) {
        const response = await axios.post(API_BASE_URL + 'patients/register', {
            email,
            password,
        });
        return response.data; // Returns success message
    }

    // Registers a new therapist
    async registerTherapist(email, password, bio, experience, qualifications, specializationNames) {
        const response = await axios.post(API_BASE_URL + 'therapists/register', {
            email,
            password,
            bio,
            experience,
            qualifications,
            specializationNames,
        });
        return response.data;
    }

    // Logs in a user (patient, therapist, or admin)
    async login(email, password) {
        const response = await axios.post(API_BASE_URL + 'auth/login', {
            email,
            password,
        });

        // If login is successful, store the user's JWT and details in local storage
        if (response.data.token) {
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    }

    // Logs out a user by removing their data from local storage
    logout() {
        localStorage.removeItem('user');
    }

    // Retrieves the current authenticated user's data from local storage
    // This data includes: { token, type: "Bearer", id, email, role }
    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
    }
}

export default new AuthService();