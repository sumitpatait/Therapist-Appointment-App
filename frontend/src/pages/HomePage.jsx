// frontend/src/pages/HomePage.jsx 

import React from 'react'; 
// Import your image directly. The path is relative to this file.
import backgroundImage from '../assets/background.jpg'; 
import './HomePage.css'; 

const HomePage = () => { 
  return ( 
    // The main container for the page
    <div className="homepage-container">
      {/* The background div with the image */}
      <div 
        className="homepage-background"
        style={{ backgroundImage: `url(${backgroundImage})` }}
      ></div>
      
      {/* Your original home page content, which will sit on top */}
      <div className="home-page-content">
        <h1>Welcome to My Therapist App!</h1> 
        <p>Your platform to connect with mental health professionals.</p> 
        <p>Please login or register to explore our services.</p> 
      </div>
    </div> 
  ); 
}; 

export default HomePage;
