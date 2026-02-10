import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';

import Header from './components/Header';

import HomePage from './pages/HomePage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Header />

        <Routes>
          <Route path="/" element={<HomePage />} />
          
        </Routes>

      </Router>
    </AuthProvider>
  );
}

export default App;
