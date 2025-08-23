// frontend/src/services/auth-header.js

export default function authHeader() {
    const user = JSON.parse(localStorage.getItem('user'));
    console.log('authHeader: user from localStorage:', user);

    if (user && user.token) {
        console.log('authHeader: Returning Authorization header with token.');
        return { Authorization: 'Bearer ' + user.token };
    } else {
        console.log('authHeader: No user or token found, returning empty header.');
        return {};
    }
}