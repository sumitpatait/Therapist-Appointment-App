// frontend/src/context/AuthContext.jsx

import React, { createContext, useState, useEffect, useContext } from 'react';
import AuthService from '../services/AuthService';

// Create the Auth Context
const AuthContext = createContext(null);

// Auth Provider component
export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true); // To handle initial loading state

  // On initial load, try to get user from local storage
  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
    setIsLoading(false); // Finished checking local storage
  }, []);

  // Login function
  const login = async (email, password) => {
    const user = await AuthService.login(email, password);
    setCurrentUser(user);
    return user;
  };

  // Logout function
  const logout = () => {
    AuthService.logout();
    setCurrentUser(null);
  };

  // Memoize the value to prevent unnecessary re-renders
  const value = React.useMemo(() => ({
    currentUser,
    login,
    logout,
    isLoading
  }), [currentUser, isLoading]);

  if (isLoading) {
    return <div>Loading authentication...</div>; // Simple loading indicator
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use the Auth Context
export const useAuth = () => {
  return useContext(AuthContext);
};